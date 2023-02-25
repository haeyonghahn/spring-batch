# spring-batch
해당 문서 출처는 [스프링 배치 - Spring Boot 기반으로 개발하는 Spring Batch](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%EB%B0%B0%EC%B9%98/dashboard) 로 작성되었습니다.

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
  * **[DB 스키마 생성 및 이해](#db-스키마-생성-및-이해)**
* **[스프링 배치 도메인 이해](#스프링-배치-도메인-이해)**
  * **[Job](#job)**
  * **[JobInstance](#jobinstance)**
  * **[JobParameter](#jobparameter)**
  * **[JobExecution](#jobexecution)**
  * **[Step](#step)**
  * **[StepExecution](#stepexecution)**
  
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

### DB 스키마 생성 및 이해
1. 스프링 배치 메타 데이터
- 스프링 배치의 실행 및 관리를 위한 목적으로 여러 도메인들(Job, Step, JobParameters..) 의 정보들을 저장, 업데이트, 조회할 수 있는 스키마를 제공한다.
- 과거, 현재의 실행에 대한 세세한 정보, 실행에 대한 성공과 실패 여부 등을 관리함으로서 배치 운용에 있어 리스크 발생시 빠른 대처가 가능하다.
- DB 와 연동할 경우 필수적으로 메타 테이블이 생성 되어야 한다.
2. DB 스키마 제공
- 파일 위치 : /org/springframework/batch/core/schema-*.sql 
- DB 유형별로 제공한다.
3. 스키마 생성 설정
- 수동 생성 : 쿼리 복사 후 직접 실행
- 자동 생성 : spring.batch.jdbc.initialize-schema 설정
  - ALWAYS
    - 스크립트 항상 실행
    - RDBMS 설정이 되어 있을 경우 내장 DB 보다 우선적으로 실행
  - EMBEDDED : 내장 DB일 때만 실행되며 스키마가 자동 생성됨, 기본값
  - NEVER 
    - 스크립트 항상 실행 안함 
    - 내장 DB 일경우 스크립트가 생성이 안되기 때문에 오류 발생 
    - 운영에서 수동으로 스크립트 생성 후 설정하는 것을 권장

![image](https://user-images.githubusercontent.com/31242766/218767492-12ed78b6-33d4-49a8-ad84-7023d3aed9ec.png)   
__Job 관련 테이블__   
- `BATCH_JOB_INSTANCE`
  - Job 이 실행될 때 JobInstance 정보가 저장되며 job_name 과 job_key 로 하나의 데이터가 저장
  - 동일한 job_name과 job_key 로 중복 저장될 수 없다.
```
- JOB_INSTANCE_ID : 고유하게 식별할 수 있는 기본 키  
- VERSION : 업데이트 될 때 마다 1씩 증가   
- JOB_NAME : Job 을 구성할 때 부여하는 Job 의 이름
- JOB_KEY : job_name과 jobParameter 를 합쳐 해싱한 값을 저장
```
- `BATCH_JOB_EXECUTION`
  - job 의 실행 정보가 저장되며 Job 생성, 시작, 종료 시간, 실행 상태, 메시지 등을 관리
```
- JOB_EXECUTION_ID : JobExecution 을 고유하게 식별할 수 있는 기본 키, JOB_INSTANCE 와 일대 다 관계
- VERSION : 업데이트 될 때마다 1씩 증가
- JOB_INSTANCE_ID : JOB_INSTANCE 의 키 저장
- CREATE_TIME : 실행(Execution)이 생성된 시점을 TimeStamp 형식으로 기록
- START_TIME : 실행(Execution)이 시작된 시점을 TimeStamp 형식으로 기록
- END_TIME : 실행이 종료된 시점을 TimeStamp으로 기록하며 Job 실행 도중 오류가 발생해서 Job 이 중단된 경우 값이 저장되지 않을 수 있음
- STATUS : 실행 상태 (BatchStatus)를 저장 (COMPLETED, FAILED, STOPPED…)
- EXIT_CODE : 실행 종료코드(ExitStatus) 를 저장 (COMPLETED, FAILED…)
- EXIT_MESSAGE : Status가 실패일 경우 실패 원인 등의 내용을 저장
- LAST_UPDATED : 마지막 실행(Execution) 시점을 TimeStamp 형식으로 기록
```
- `BATCH_JOB_EXECUTION_PARAMS`
  - Job과 함께 실행되는 JobParameter 정보를 저장
```
- JOB_EXECUTION_ID : JobExecution 식별 키, JOB_EXECUTION 과는 일대다 관계
- TYPE_CD : STRING, LONG, DATE, DUBLE 타입정보
- KEY_NAME : 파라미터 키 값
- STRING_VAL : 파라미터 문자 값
- DATE_VAL : 파라미터 날짜 값
- LONG_VAL : 파라미터 LONG 값
- DOUBLE_VAL : 파라미터 DOUBLE 값
- IDENTIFYING : 식별여부 (TRUE, FALSE)
```
- `BATCH_JOB_EXECUTION_CONTEXT`
  - Job 의 실행 동안 여러가지 상태 정보, 공유 데이터를 직렬화(Json 형식)해서 저장
  - Step 간 서로 공유 가능함
```
- JOB_EXECUTION_ID : JobExecution 식별 키, JOB_EXECUTION 마다 각 생성
- SHORT_CONTEXT : JOB 의 실행 상태정보, 공유데이터 등의 정보를 문자열로 저장
- SERIALIZED_CONTEXT : 직렬화(serialized)된 전체 컨텍스트
```
  
__Step 관련 테이블__   
- `BATCH_STEP_EXECUTION`
  - Step 의 실행 정보가 저장되며 생성, 시작, 종료 시간, 실행 상태, 메시지 등을 관리
```
- STEP_EXECUTION_ID : Step 의 실행정보를 고유하게 식별할 수 있는 기본 키
- VERSION : 업데이트 될 때마다 1씩 증가
- STEP_NAME : Step 을 구성할 때 부여하는 Step 이름
- JOB_EXECUTION_ID : JobExecution 기본키, JobExecution 과는 일대 다 관계
- START_TIME : 실행(Execution)이 시작된 시점을 TimeStamp 형식으로 기록
- END_TIME : 실행이 종료된 시점을 TimeStamp 으로 기록하며 Job 실행 도중 오류가 발생해서 Job 이 중단된 경우 값이 저장되지 않을 수 있음
- STATUS : 실행 상태 (BatchStatus)를 저장 (COMPLETED, FAILED, STOPPED…)
- COMMIT_COUNT : 트랜잭션 당 커밋되는 수를 기록
- READ_COUNT : 실행시점에 Read한 Item 수를 기록
- FILTER_COUNT : 실행도중 필터링된 Item 수를 기록
- WRITE_COUNT : 실행도중 저장되고 커밋된 Item 수를 기록
- READ_SKIP_COUNT : 실행도중 Read가 Skip 된 Item 수를 기록
- WRITE_SKIP_COUNT : 실행도중 write가 Skip된 Item 수를 기록
- PROCESS_SKIP_COUNT : 실행도중 Process가 Skip 된 Item 수를 기록
- ROLLBACK_COUNT : 실행도중 rollback이 일어난 수를 기록
- EXIT_CODE : 실행 종료코드(ExitStatus) 를 저장 (COMPLETED, FAILED…)
- EXIT_MESSAGE : Status가 실패일 경우 실패 원인 등의 내용을 저장
- LAST_UPDATED : 마지막 실행(Execution) 시점을 TimeStamp 형식으로 기록
```
- `BATCH_STEP_EXECUTION_CONTEXT`
  - Step 의 실행동안 여러가지 상태 정보, 공유 데이터를 직렬화(Json 형식)해서 저장
  - Step 별로 저장되며 Step 간 서로 공유할 수 없음
```
- STEP_EXECUTION_ID : StepExecution 식별 키, STEP_EXECUTION 마다 각 생성
- SHORT_CONTEXT : STEP 의 실행 상태정보, 공유데이터 등의 정보를 문자열로 저장
- SERIALIZED_CONTEXT : 직렬화(serialized)된 전체 컨텍스트
```

## 스프링 배치 도메인 이해
### Job
__1. 기본 개념__   
- 배치 계층 구조에서 가장 상위에 있는 개념으로서 하나의 배치작업 자체를 의미한다.
  - `API 서버의 접속 로그 데이터를 통계 서버로 옮기는 배치` 인 Job 자체를 의미한다. 
- Job Configuration 을 통해 생성되는 객체 단위로서 배치작업을 어떻게 구성하고 실행할 것인지 전체적으로 설정하고 명세해 놓은 객체이다.
- 배치 Job 을 구성하기 위한 최상위 인터페이스이며 스프링 배치가 기본 구현체를 제공한다.
- 여러 Step 을 포함하고 있는 컨테이너로서 반드시 한개 이상의 Step으로 구성해야 한다.

__2. 기본 구현체__   
- SimpleJob
  - 순차적으로 Step 을 실행시키는 Job
  - 모든 Job에서 유용하게 사용할 수 있는 표준 기능을 갖고 있음
- FlowJob
  - 특정한 조건과 흐름에 따라 Step 을 구성하여 실행시키는 Job
  - Flow 객체를 실행시켜서 작업을 진행함
  
![image](https://user-images.githubusercontent.com/31242766/219631840-8c354f3c-3659-40a5-8140-f314623ab1c8.png)

### JobInstance
- Job 이 실행될 때 생성되는 Job 의 논리적 실행 단위 객체로서 고유하게 식별 가능한 작업 실행을 나타낸다.
- Job 의 설정과 구성은 동일하지만 Job 이 실행되는 시점에 처리하는 내용은 다르기 때문에 Job 의 실행을 구분해야 한다.
  - 예를 들어 하루에 한 번 씩 배치 Job이 실행된다면 매일 실행되는 각각의 Job 을 JobInstance 로 표현한다.
- JobInstance 생성 및 실행
  - 처음 시작하는 Job + JobParameter 일 경우 새로운 JobInstance 생성
  - 이전과 동일한 Job + JobParameter 으로 실행 할 경우 이미 존재하는 JobInstance 리턴
    - 내부적으로 JobName + jobKey (jobParametes 의 해시값) 를 가지고 JobInstance 객체를 얻는다.
- Job 과는 1:M 관계

![image](https://user-images.githubusercontent.com/31242766/220130667-967a96bb-39c2-4d2a-9cc5-8c764a0ee7d2.png)
![image](https://user-images.githubusercontent.com/31242766/220130813-3943fd8c-328f-49e0-9e71-74016e799637.png)

### JobParameter
__기본 개념__   
- Job을 실행할 때 함께 포함되어 사용되는 파라미터를 가진 도메인 객체이다.
- 하나의 Job에 존재할 수 있는 여러개의 JobInstance를 구분하기 위한 용도이다.
- JobParameters와 JobInstance는 1:1 관계

__생성 및 바인딩__   
- 어플리케이션 실행 시 주입
  - Java -jar LogBatch.jar name=user1 seq(long)=2L date(date)=2021/01/01 age(double)=16.5
- 코드로 생성
  - JobParameterBuilder, DefaultJobParametersConverter
- SpEL 이용 
  - @Value(“#{jobParameter[requestDate]}”), @JobScope, @StepScope 선언 필수

__BATCH_JOB_EXECUTION_PARAM 테이블과 매핑__   
- JOB_EXECUTION 과 1:M 의 관계

![image](https://user-images.githubusercontent.com/31242766/220138748-8dfce826-6c07-46c3-ae7f-e8147ff196c7.png)

### JobExecution
__기본 개념__    
- JobInstance 에 대한 한번의 시도를 의미하는 객체로서 Job 실행 중에 발생한 정보들을 저장하고 있는 객체이다.
  - 시작시간, 종료시간, 상태(시작됨, 완료, 실패), 종료상태의 속성을 가진다.
- JobInstance 과의 관계
  - JobExecution은 `FAILED` 또는 `COMPLETED` 등의 Job 실행 결과 상태를 가지고 있다.
  - JobExecution 의 실행 상태 결과가 `COMPLETED` 면 JobInstance 실행이 완료된 것으로 간주해서 재실행이 불가하다.
  - JobExecution 의 실행 상태 결과가 `FAILED` 면 JobInstance 실행이 완료되지 않은 것으로 간주해서 재실행이 가능하다.
    - JobParameter 가 동일한 값으로 Job 을 실행할지라도 JobInstance 를 계속 실행할 수 있다.
- JobExecution 의 실행 상태 결과가 `COMPLETED` 될 때까지 하나의 JobInstance 내에서 여러 번의 시도가 생길 수 있다.

__BATCH_JOB_EXECUTION 테이블과 매핑__   
JobInstance 와 JobExecution 는 1:M 의 관계로서 JobInstance 에 대한 성공/실패의 내역을 가지고 있다.   
![image](https://user-images.githubusercontent.com/31242766/220632213-ef69b800-104f-4b62-88d0-4624ae7a1cc9.png)
![image](https://user-images.githubusercontent.com/31242766/220657188-dd6ad3bc-2a16-4871-adb6-79295ea58781.png)
![image](https://user-images.githubusercontent.com/31242766/220657034-101a3c96-f43a-486a-9648-1f97e3920479.png)

### Step
__기본 개념__    
- Batch job을 구성하는 독립적인 하나의 단계로서 실제 배치 처리를 정의하고 컨트롤하는 데 필요한 모든 정보를 가지고 있는 도메인 객체이다.
- 단순한 단일 태스크 뿐 아니라 입력과 처리 그리고 출력과 관련된 복잡한 비즈니스 로직을 포함하는 모든 설정들을 담고 있다.
- 배치작업을 어떻게 구성하고 실행할 것인지 Job 의 세부 작업을 Task 기반으로 설정하고 명세해 놓은 객체이다.
- 모든 Job은 하나 이상의 step으로 구성된다.

__기본 구현체__   
- TaskletStep   
  - 가장 기본이 되는 클래스로서 Tasklet 타입의 구현체들을 제어한다.
- PartitionStep
  - 멀티 스레드 방식으로 Step 을 여러 개로 분리해서 실행한다.
- JobStep
  - Step 내에서 Job 을 실행하도록 한다.
- FlowStep
  - Step 내에서 Flow 를 실행하도록 한다.
  
![image](https://user-images.githubusercontent.com/31242766/221363023-91405db1-f27a-4975-ae37-7015b433cf86.png)
![image](https://user-images.githubusercontent.com/31242766/221363087-cee568c0-c2e5-4319-a953-28c805a0da0f.png)
![image](https://user-images.githubusercontent.com/31242766/221363129-2e3a0d75-e190-427c-ad2f-c4cfa82e3e2f.png)

### StepExecution
__기본 개념__   
- Step 에 대한 한번의 시도를 의미하는 객체로서 Step 실행 중에 발생한 정보들을 저장하고 있는 객체이다.
  - 시작시간, 종료시간, 상태(시작됨, 완료, 실패), commit count, rollback count 등의 속성을 가진다.
- Step 이 매번 시도될 때마다 생성되며 각 Step 별로 생성된다.
- Job 이 재시작하더라도 이미 성공적으로 완료된 Step은 재실행되지 않고 실패한 Step만 실행된다.
- 이전 단계 Step이 실패해서 현재 Step을 실행하지 않았다면 StepExecution을 생성하지 않는다. Step이 실제로 시작됐을 때만 StepExecution을 생성한다.
- JobExecution과의 관계
  - Step의 StepExecution이 모두 정상적으로 완료되어야 JobExecution이 정상적으로 완료된다.
  - Step의 StepExecution 중 하나라도 실패하면 JobExecution 은 실패한다.

__BATCH_STEP_EXECUTION 테이블과 매핑__   
- JobExecution 와 StepExecution 는 1:M 의 관계
- 하나의 Job 에 여러 개의 Step 으로 구성했을 경우 각 StepExecution 은 하나의 JobExecution 을 부모로 가진다.

![image](https://user-images.githubusercontent.com/31242766/221369727-21c6ec66-b5da-4081-8848-24129c06b9ee.png)
![image](https://user-images.githubusercontent.com/31242766/221369744-719fadde-acc9-4cfb-890d-5ec2642b981b.png)
![image](https://user-images.githubusercontent.com/31242766/221369795-06c4c5f9-8e76-422b-821e-957680842b4b.png)

