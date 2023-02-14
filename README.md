# spring-batch
1. 배치 핵심 패턴
- Read : 데이터베이스, 파일, 큐에서 다량의 데이터를 조회한다.
- Process : 특정 방법으로 데이터를 가공한다.
- Write : 데이터를 수정된 양식으로 다시 저장한다.
2. 배치 시나리오
- 배치 프로세스를 주기적으로 커밋
- 동시 다발적인 Job 의 배치 처리, 대용량 병렬 처리
- 실패 후 수동 또는 스케줄링에 의한 시작
- 의존관계가 있는 step 여러 개를 순차적으로 처리
- 조건적 Flow 구성을 통한 체계적이고 유연한 배치 모델 구성
- 반복, 재시도, Skip 처리
3. 아키텍처
- Application
  - 스프링 배치 프레임워크를 통해 개발자가 만든 모든 배치 Job 과 커스텀 코드를 포함한다.
  - 개발자는 업무 로직의 구현에만 집중하고 공통적인 기반 기술은 프레임워크가 담당하게 한다.
- Batch Core
  - Job을 실행, 모니터링, 관리하는 API 로 구성되어 있다.
  - JobLauncher, Job, Step, Flow 등이 속한다.
- Batch Infrastructure
  - Application, Core 모두 공통 Infrastructure 위에서 빌드한다.
  - Job 실행의 흐름과 처리를 위한 틀을 제공한다.
  - Reader, Processor Writer, Skip, Retry 등이 속한다.
## 목차
* **[스프링 배치 시작](#스프링-배치-시작)**
  * **[프로젝트 구성 및 의존성 설정](#프로젝트-구성-및-의존성-설정)**
  * **[Hello Spring Batch 시작하기](#hello-spring-batch-시작하기)**
  
## 스프링 배치 시작
### 프로젝트 구성 및 의존성 설정
__dependency__   
```gradle
implementation 'org.springframework.boot:spring-boot-starter-batch'
```
__스프링 배치 활성화__   
- `@EnableBatchProcessing`   
스프링 배치가 작동하기 위해 선언해야 하는 어노테이션이다.   
```java
@EnableBatchProcessing
@SpringBootApplication
public class SpringbatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbatchApplication.class);
    }
}
```
- 총 4개의 설정 클래스를 실행시키며 스프링 배치의 모든 초기화 및 실행 구성이 이루어진다.
- 스프링 부트 배치의 자동 설정 클래스가 실행됨으로 빈으로 등록된 모든 Job 을 검색해서 초기화와 동시에 Job 을 수행하도록 구성된다.

__스프링 배치 초기화 설정 클래스__    
![image](https://user-images.githubusercontent.com/31242766/218739670-22c8437d-4232-4a06-b598-a671b7603f5b.png)   
1. BatchAutoConfiguration
- 스프링 배치가 초기화될 때 자동으로 실행되는 설정 클래스이다.
- Job 을 수행하는 JobLauncherApplicationRunner 빈을 생성
2. SimpleBatchConfiguration 
- JobBuilderFactory 와 StepBuilderFactory 생성한다.
- 스프링 배치의 주요 구성 요소가 생성된다. 프록시 객체로 생성된다.
3. BatchConfigurerConfiguration
- BasicBatchConfigurer 
  - SimpleBatchConfiguration 에서 생성한 프록시 객체의 실제 대상 객체를 생성하는 설정 클래스이다.
  - 빈으로 의존성 주입 받아서 주요 객체들을 참조해서 사용할 수 있다.
- JpaBatchConfigurer 
  - JPA 관련 객체를 생성하는 설정 클래스이다.
- 사용자 정의 BatchConfigurer 인터페이스를 구현하여 사용할 수 있다.

### Hello Spring Batch 시작하기
1. @Congifuration 선언
- 하나의 배치 Job을 정의하고 빈 설정
2. JobBuilderFactory
- Job을 생성하는 빌더 팩토리
3. StepBuilderFactory
- Step을 생성하는 빌더 팩토리
4. Job
- helloJob 이름으로 Job 생성
5. Step
- helloStep 이름으로 Step 생성
6. tasklet
- Step 안에서 단일 테스크로 수행되는 로직 구현
7. Job -> Step 실행 -> Tasklet 을 실행   
Job 이 구동되면 Step 을 실행하고 Step 이 구동되면 Tasklet 을 실행하도록 설정한다.   
![image](https://user-images.githubusercontent.com/31242766/218743480-63d2d0f4-7f9a-4161-a319-f8287a854f2c.png)   
![image](https://user-images.githubusercontent.com/31242766/218744471-9dbbe3fd-6118-4a9b-adb1-bb1c7edbf00f.png)
