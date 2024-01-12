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
  * **[StepContribution](#stepcontribution)**
  * **[ExecutionContext](#executioncontext)**
  * **[JobRepository](#jobrepository)**
  * **[JobLauncher](#joblauncher)**
* **[스프링 배치 실행 - Job](#스프링-배치-실행---job)**  
  * **[배치 초기화 설정](#배치-초기화-설정)**
  * **[JobBuilderFactory / JobBuilder](#jobbuilderfactory--jobbuilder)**
  * **[SimpleJob - 개념 및 API 소개](#simplejob---개념-및-api-소개)**
  * **[SimpleJob - start() / next()](#simplejob---start--next)**
  * **[SimpleJob - validator()](#simplejob---validator)**
  * **[SimpleJob - preventRestart()](#simplejob---preventrestart)**
  * **[SimpleJob - incrementer()](#simplejob---incrementer)**
  * **[SimpleJob 아키텍처](#simplejob-아키텍처)**
* **[스프링 배치 실행 - Step](#스프링-배치-실행---step)**
  * **[StepBuilderFactory](#stepbuilderfactory)**
  * **[TaskletStep - 개념 및 API 소개](#taskletstep---개념-및-api-소개)**
  * **[TaskletStep - tasklet()](#taskletstep---tasklet)**
  * **[TaskletStep - startLimit() / allowStartIfComplete()](#taskletstep---startlimit--allowstartifcomplete)**
  * **[TaskletStep 아키텍처](#taskletstep-아키텍처)**
  * **[JobStep](#jobstep)**
* **[스프링 배치 실행 - Flow](#스프링-배치-실행---flow)**
  * **[FlowJob - 개념 및 API 소개](#flowjob---개념-및-api-소개)**
  * **[FlowJob - start() / next()](#FlowJob---start--next)**
* **[스프링 배치 청크 프로세스 이해](#스프링-배치-청크-프로세스-이해)**
  * **[Chunk](#chunk)**
  * **[ChunkOrientedTasklet](#chunkOrientedtasklet)**
  * **[ChunkProvider](#chunkprovider)**
  * **[ChunkProcessor](#chunkprocessor)**
  * **[ItemReader](#itemReader)**
  * **[ItemWriter](#itemwriter)**
  * **[ItemProcessor](#itemprocessor)**
  * **[ItemStream](#itemstream)**
  * **[Chunk Process 아키텍처](#chunk-process-아키텍처)**
* **[스프링 배치 청크 프로세스 활용 - ItemReader](#스프링-배치-청크-프로세스-활용---itemreader)**
  * **[FlatFileItemReader - 개념 및 API 소개](#flatfileitemreader---개념-및-api-소개)**
  * **[FlatFileItemReader - delimetedlinetokenizer](#flatfileitemreader---delimetedlinetokenizer)**
  * **[FlatFileItemReader - fixedlengthtokenizer](#flatfileitemreader---fixedlengthtokenizer)**
  * **[FlatFileItemReader - Exception Handling](#flatfileitemreader---exception-handling)**
  * **[JsonItemReader](#jsonitemreader)**
  * **[DB - Cursor & Paging 이해](#db---cursor-&-paging-이해)**
  * **[DB - JdbcCursorItemReader](#db---jdbccursoritemreader)**
  * **[DB - JpaCursorItemReader](#db---jpacursoritemreader)**
* **[스프링 배치 테스트 및 운영](스프링-배치-테스트-및-운영)**
  * **[Spring Batch Test](#spring-batch-test)**
  * **[JobExplorer / JobRegistry / JobOperator](#jobexplorer--jobregistry--joboperator)**
  
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

### StepContribution
__기본 개념__   
- 청크 프로세스의 변경 사항을 버퍼링 한 후 StepExecution 상태를 업데이트하는 도메인 객체이다.
- 청크 커밋 직전에 StepExecution 의 apply 메서드를 호출하여 상태를 업데이트 한다.
- ExitStatus 의 기본 종료코드 외 사용자 정의 종료코드를 생성해서 적용 할 수 있다.

__구조__   
![image](https://user-images.githubusercontent.com/31242766/221566083-ab985b58-9512-41e1-880a-e189d6b12390.png)
![image](https://user-images.githubusercontent.com/31242766/221566127-c61f98ca-3395-40b0-92f7-455a9c878d66.png)

### ExecutionContext
__기본 개념__    
- 프레임워크에서 유지 및 관리하는 키/값으로 된 컬렉션으로 StepExecution 또는 JobExecution 객체의 상태(state)를 저장하는 공유 객체이다.
- DB 에 직렬화한 값으로 저장된다 -> { "key" : "value" }
- 공유 범위   
  - Step 범위 : 각 Step 의 StepExecution 에 저장되며 Step 간 서로 공유 안된다.
  - Job 범위 : 각 Job의 JobExecution 에 저장되며 Job 간 서로 공유 안되며 해당 Job의 Step 간 서로 공유된다.
- Job 재시작시 이미 처리한 Row 데이터는 건너뛰고 이후로 수행하도록 할 때 상태 정보를 활용한다.

__구조__   
![image](https://user-images.githubusercontent.com/31242766/222970008-8a6e8549-d98b-4b78-885d-12d996f234e2.png)
![image](https://user-images.githubusercontent.com/31242766/222970172-fe2fdadf-d397-4ff1-9a31-84114763f3bf.png)
![image](https://user-images.githubusercontent.com/31242766/222970194-89b17a93-edcf-4c64-8ed9-7b08cc637469.png)

### jobRepository
__기본 개념__   
- 배치 작업 중의 정보를 저장하는 저장소 역할
- Job이 언제 수행되었고 언제 끝났으며 몇 번이 실행되었고 실행에 대한 결과 등의 배치 작업의 수행과 관련된 모든 meta data 를 저장한다.
  - JobLauncher, Job, Step 구현체 내부에서 CRUD 기능을 처리한다.

![image](https://user-images.githubusercontent.com/31242766/223159286-9af5cca1-bbde-4f45-853e-158db71af005.png)
![image](https://user-images.githubusercontent.com/31242766/223159348-a07c43aa-3f5e-4e38-b6ac-20d0029b7928.png)

__jobRepository 설정__    
- @EnableBatchProcessing 어노테이션만 선언하면 JobRepository 가 자동으로 빈으로 생성된다.
- BatchConfigurer 인터페이스를 구현하거나 BasicBatchConfigurer 를 상속해서 JobRepository 설정을 커스터마이징 할 수 있다.
  - JDBC 방식으로 설정 `JobRepositoryFactoryBean`
    - 내부적으로 AOP 기술를 통해 트랜잭션 처리를 해주고 있다.
    - 트랜잭션 isolation 의 기본값은 SERIALIZEBLE 로 최고 수준, 다른 레벨(READ_COMMITED, REPEATABLE_READ)로 지정 가능하다.
    - 메타테이블의 Table Prefix 를 변경할 수 있다. 기본 값은 `BATCH_` 이다.
  - In Memory 방식으로 설정 `MapJobRepositoryFactoryBean`
    - 성능 등의 이유로 도메인 오브젝트를 굳이 데이터베이스에 저장하고 싶지 않을 경우
    - 보통 Test 나 프로토타입의 빠른 개발이 필요할 때 사용한다.
    
__JDBC__   
```java
@Override
protected JobRepository createJobRepository() throws Exception {
    JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
    factory.setDataSource(dataSource);
    factory.setTransactionManager(transactionManager);
    factory.setIsolationLevelForCreate("ISOLATION_SERIALIZABLE"); // isolation 수준, 기본값은 "ISOLATION_SERIALIZABLE"
    factory.setTablePrefix(“SYSTEM_"); // 테이블 Prefix, 기본값은 “BATCH_”, BATCH_JOB_EXECUTION 가 SYSTEM_JOB_EXECUTION 으로 변경됨
    factory.setMaxVarCharLength(1000); // varchar 최대 길이(기본값 2500)
    return factory.getObject(); // Proxy 객체가 생성됨 (트랜잭션 Advice 적용 등을 위해 AOP 기술 적용)
}
```

__In Memory__   
```java
@Override
protected JobRepository createJobRepository() throws Exception {
    MapJobRepositoryFactoryBean factory = new MapJobRepositoryFactoryBean();
    factory.setTransactionManager(transactionManager); // ResourcelessTransactionManager 사용
    return factory.getObject();
}
```

### JobLauncher
__기본 개념__   
- 배치 Job을 실행시키는 역할을 한다.
- Job과 Job Parameters를 인자로 받으며 요청된 배치 작업을 수행한 후 최종 client에게 JobExecution을 반환한다.
- 스프링 부트 배치가 구동이 되면 JobLauncher 빈이 자동 생성된다.
- Job 실행
  - `JobLauncher.run(Job, JobParameters)`
  - 스프링 부트 배치에서는 JobLauncherApplicationRunner가 자동적으로 JobLauncher을 실행시킨다.
  - 동기적 실행
    - taskExecutor를 SyncTaskExecutor 로 설정할 경우 (기본값은 SyncTaskExecutor)
    - JobExecution을 획득하고 배치 처리를 최종 완료한 이후 Client 에게 JobExecution 을 반환한다.
    - 스케줄러에 의한 배치처리에 적합하다 - 배치처리시간이 길어도 상관이 없는 경우
  - 비동기적 실행
    - taskExecutor가 SimpleAsyncTaskExecutor로 설정할 경우
    - JobExecution을 획득한 후 Client에게 바로 JobExecution을 반환하고 배치처리를 완료한다.
    - HTTP 요청에 의한 배치처리에 적합하다 - 배치처리 시간이 길 경우 응답이 늦어지지 않도록 한다.
    
__구조__   
![image](https://user-images.githubusercontent.com/31242766/223416836-d8cd2101-3ce5-4089-aa60-52e74939313d.png)
![image](https://user-images.githubusercontent.com/31242766/223416891-b3e59f45-f18a-4109-aea0-a9d3ae9b810b.png)

## 스프링 배치 실행 - Job
### 배치 초기화 설정
__JobLauncherApplicationRunner__   
- Spring Batch 작업을 시작하는 ApplicationRunner 로서 BatchAutoConfiguration 에서 생성된다.
- 스프링 부트에서 제공하는 ApplicationRunner 의 구현체로 어플리케이션이 정상적으로 구동되자 마다 실행된다.
- 기본적으로 빈으로 등록된 모든 job 을 실행시킨다.

__BatchProperties__   
- Spring Batch 의 환경 설정 클래스
- Job 이름, 스키마 초기화 설정, 테이블 Prefix 등의 값을 설정할 수 있다.
- application.properties, application.yml 파일에 설정한다.
```yml
batch:
  job:
    names: ${job.name:NONE}
  initialize-schema: NEVER
  tablePrefix: SYSTEM
```

__Job 실행 옵션__   
- 지정한 Batch Job만 실행하도록 할 수 있다.
- spring.batch.job.names: ${job.name:NONE}
- 어플리케이션 실행 시 Program arguments 로 job 이름을 입력한다.
  - `--job.name=helloJob`
  - `--job.name=helloJob,simpleJob` (하나 이상의 job을 실행할 경우 쉼표로 구분해서 입력한다.)

### JobBuilderFactory / JobBuilder
__스프링 배치는 Job 과 Step 을 쉽게 생성 및 설정할 수 있도록 util 성격의 빌더 클래스들을 제공한다.__   
__JobBuilderFactory__   
- JobBuilder 를 생성하는 팩토리 클래스로서 get(String name) 메서드 제공한다.
- JobBuilderFactory.get("jobName")
  - "jobName" 은 스프링 배치가 Job 을 실행시킬 때 참조하는 Job 이름이다.
  
__JobBuilder__   
- Job 을 구성하는 설정 조건에 따라 두 개의 하위 빌더 클래스를 생성하고 실제 Job 생성을 위임한다.
- SimpleJobBuilder
  - SimpleJob 을 생성하는 Builder 클래스
  - Job 실행과 관련된 여러 설정 API 를 제공한다.
- FlowJobBuilder
  - FlowJob 을 생성하는 Builder 클래스
  - 내부적으로 FlowBuilder 를 반환함으로써 Flow 실행과 관련된 여러 설정 API 를 제공한다.
  
__아키텍처__   
![image](https://user-images.githubusercontent.com/31242766/224045399-ec5781d6-529b-4443-9baa-624763e8a336.png)

__클래스 상속 구조__   
![image](https://user-images.githubusercontent.com/31242766/224045506-a5ba32d2-d8fb-4e38-a020-abb17c2d3964.png)

### SimpleJob - 개념 및 API 소개
__기본 개념__   
- SimpleJob 은 Step 을 실행시키는 Job 구현체로서 SimpleJobBuilder 에 의해 생성된다.
- 여러 단계의 Step 으로 구성할 수 있으며 Step 을 순차적으로 실행시킨다.
- 모든 Step 의 실행이 성공적으로 완료되어야 Job 이 성공적으로 완료된다.
- 맨 마지막에 실행한 Step 의 BatchStatus 가 Job 의 최종 BatchStatus 가 된다.

__흐름__   
![image](https://user-images.githubusercontent.com/31242766/224344663-7e69f07e-9398-4bef-b2b8-100e3652c064.png)
![image](https://user-images.githubusercontent.com/31242766/224354727-e9460fff-9bcd-44b7-915f-3d6c178b94bc.png)
![image](https://user-images.githubusercontent.com/31242766/224354770-89694bec-4538-46e5-a874-64cf0243b759.png)

### SimpleJob - start() / next()
```java
public Job batchJob() {
  return jobBuilderFactory.get("batchJob")
      .start(Step)  //처음 실행 할 Step 설정, 최초 한번 설정, SimpleJobBuilder가 생성되고 반환된다.
      .next(Step)   //다음에 실행 할 Step들을 순차적으로 연결하도록 설정한다.
      .incrementer() //여러 번 설정이 가능하며 모든 next()의 Step이 종료가 되면 Job이 종료된다.
      .validator()
      .preventRestart()
      .listener()
      .build();
}
```
![image](https://user-images.githubusercontent.com/31242766/224552504-377c6aec-b24c-4fb0-b62c-1c5d9f1420eb.png)

### SimpleJob - validator()
__기본 개념__   
- Job 실행에 꼭 필요한 파라미터를 검증하는 용도
- DefaultJobParametersValidator 구현체를 지원하며 좀 더 복잡한 제약 조건이 있다면 인터페이스를 직접 구현할 수도 있다.

__구조__   
![image](https://user-images.githubusercontent.com/31242766/224729410-186d9fd6-12bd-4404-af82-af8021d68d81.png)

```java
public Job batchJob() {
    return jobBuilderFactory.get(“batchJob")
      .start()
      .next()
      .incrementer()
      .validator(JobParametersValidator)
      .preventRestart()
      .listener()
      .build();
}
```
__DefaultJobParametersValidator 흐름도__   
![image](https://user-images.githubusercontent.com/31242766/224730488-31d682a3-f7ef-48b3-8a39-63a31eb49016.png)

### SimpleJob - preventRestart()
__기본 개념__   
- Job 의 재 시작 여부를 설정
- 기본 값은 true 이며 false 로 설정 시 `Job은 재시작을 지원하지 않는다` 는 의미이다.
- Job 이 실패해도 재 시작이 안되며 Job 을 재 시작하려고 하면 JobRestartException이 발생한다.
- 재시작과 관련 있는 기능으로 Job 을 처음 실행하는 것과는 상관이 없다.

__흐름도__   
![image](https://user-images.githubusercontent.com/31242766/225856836-2e92d23a-5efe-4fd0-a5ac-fcb44463be71.png)
- Job의 실행이 처음이 아닌 경우는 Job 의 성공/실패와 상관없이 preventRestart 설정 값에 따라서 실행 여부를 판단한다.

### SimpleJob - incrementer()
__기본 개념__   
- JobParameters 에서 필요한 값을 증가시켜 다음에 사용될 JobParameters 오브젝트를 리턴
- 기존의 JobParameter 변경없이 Job을 여러 번 시작하고자 할 때
- RunIdIncrementer 구현체를 지원하면 인터페이스를 직접 구현할 수 있다.

![image](https://github.com/haeyonghahn/spring-batch/assets/31242766/ac3d345c-6737-4de4-a30f-24e18fe71907)

### SimpleJob 아키텍처
![image](https://github.com/haeyonghahn/spring-batch/assets/31242766/a3d48e2c-d373-4261-8067-3bd76a24f393)

__클래스 상속 관계도__   
![image](https://github.com/haeyonghahn/spring-batch/assets/31242766/a8690700-11e1-4148-81bf-f8b51a25b73c)

## 스프링 배치 실행 - Step
### StepBuilderFactory
__StepBuilderFactory__    
- StepBuilder를 생성하는 팩토리 클래스로서 get(String name) 메서드 제공
- StepBuilderFactory.get("stepName")
  - "stepName"으로 Step을 생성

__StepBuilder__   
- Step을 구성하는 설정 조건에 따라 다섯 개의 하위 빌더 클래스를 생성하고 실제 Step생성을 위임한다.
- TaskletStepBuilder
  - TaskletStep을 생성하는 기본 빌더 클래스
- SimpleStepBuilder
  - TaskletStep을 생성하며 내부적으로 청크기반의 작업을 처리하는 ChunkOrientedTasklet 클래스를 생성한다.
- PartitionStepBuilder
  - PartitionStep을 생성하며 멀티 스레드 방식으로 Job을 실행한다.
- JobStepBuilder
  - JobStep을 생성하여 Step 안에서 Job을 실행한다.
- FlowStepBuilder
  - FlowStep을 생성하여 Step 안에서 Flow를 실행한다.
 
![image](https://github.com/haeyonghahn/spring-batch/assets/31242766/d12df058-5e60-4912-b9f2-0c7cc419bb26)   
![image](https://github.com/haeyonghahn/spring-batch/assets/31242766/6ee8f8cd-c6c6-475e-84d1-e033a14b9e6f)

### TaskletStep - 개념 및 API 소개
__기본 개념__   
- 스프링 배치에서 제공하는 Step의 구현체로서 Tasklet을 실행시키는 도메인 객체이다.
- RepeatTemplate을 사용해서 Tasklet의 구문을 트랜잭션 경계 내에서 반복해서 실행한다.
- Task 기반과 Chunk 기반으로 나누어서 Tasklet을 실행한다.

__Task vs Chunk 기반 비교__   
- 스프링 배치에서 Step의 실행 단위는 크게 2가지로 나뉜다.
  - chunk 기반
    - 하나의 큰 덩어리를 n개씩 나눠서 실행한다는 의미로 대량 처리를 하는 경우 효과적으로 설계 되었다.
    - ItemReader, ItemProcessor, ItemWriter를 사용하며 청크 기반 전용 Tasklet인 ChunkOrientedTasklet 구현체가 제공된다.
  - Task 기반
    - ItemReader와 ItemWriter와 같은 청크 기반의 작업보다 단일 작업 기반으로 처리되는 것이 더 효율적인 경우
    - 주로 Tasklet 구현체를 만들어 사용
    - 대량 처리를 하는 경우 chunk 기반에 비해 더 복잡한 구현 필요

![image](https://github.com/haeyonghahn/spring-batch/assets/31242766/d5099800-e76d-47dc-81f1-744998c7902f)
![image](https://github.com/haeyonghahn/spring-batch/assets/31242766/d8fb9379-da1d-4f1a-b27a-b7be7e66b06d)

### TaskletStep - tasklet()
__기본 개념__   
- Tasklet 타입의 클래스를 설정한다.
  - Tasklet
    - Step 내에서 구성되고 실행되는 도메인 객체로서 주로 단일 태스크를 수행하기 위한 것
    - TaskletStep에 의해 반복적으로 수행되며 반환값에 따라 계속 수행 혹은 종료한다.
    - `RepeatStatus`은 Tasklet의 반복 여부 상태 값
      - `RepeatStatus.FINISHED` : Tasklet 종료, RepeatStatus을 null로 반환하면 RepeatStatus.FINISHED로 해석된다.
      - `RepeatStatus.CONTINUABLE` : Tasklet 반복
      - RepeatStatus.FINISHED가 리턴되거나 실패 예외가 던져지기 전까지 TaskletStep에 의해 while 문 안에서 반복적으로 호출된다.
- 익명 클래스 혹은 구현 클래스를 만들어서 사용한다.
- 이 메소드를 실행하게 되면 TaskletStepBuilder가 반환되어 관련 API를 설정할 수 있다.
- Step에 오직 하나의 Tasklet 설정이 가능하며 두 개 이상을 설정했을 경우 마지막에 설정한 객체가 실행된다.

### TaskletStep - startLimit() / allowStartIfComplete()
#### startLimit()
__기본 개념__   
- Step의 실행 횟수를 조정할 수 있다.
- Step마다 설정할 수 있다.
- 설정 값을 초과해서 다시 실행하려고 하면 StartLimitExceededException이 발생
- start-limit의 디폴트 값은 Integer.MAX_VALUE

#### allowStartIfComplete()
__기본 개념__   
- 재시작 가능한 job에서 Step의 이전 성공 여부와 상관없이 항상 step을 실행하기 위한 설정
- 실행마다 유효성을 검증하는 Step이나 사전 작업이 꼭 필요한 Step 등
- 기본적으로 COMPLETED 상태를 가진 Step은 Job 재시작 시 실행하지 않고 스킵한다.
- allowStartIfComplete가 `true`로 설정된 step은 항상 실행한다.

### TaskletStep 아키텍처
![image](https://github.com/haeyonghahn/spring-batch/assets/31242766/7383c4d6-6b99-40ca-af13-068a746baa9e)
![image](https://github.com/haeyonghahn/spring-batch/assets/31242766/28a79448-4f23-4bbe-b388-15bff5f8b9c4)

### JobStep
- Job 에 속하는 Step 중 외부의 Job 을 포함하고 있는 Step
- 외부의 Job 이 실패하면 해당 Step 이 실패하므로 결국 최종 기본 Job 도 실패한다.
- 모든 메타데이터는 기본 Job 과 외부 Job 별로 각각 저장된다.
- 커다란 시스템을 작은 모듈로 쪼개고 job의 흐름를 관리하고자 할 때 사용할 수 있다.

```java
public Step jobStep() {
  return stepBuilderFactory.get("jobStep")		// StepBuilder를 생성하는 팩토리, Step의 이름을 매개변수로 받음
    .job(Job)						// JobStep 내에서 실행될 Job 설정, JobStepBuilder 반환
    .launcher(JobLauncher)				// Job을 실행할 JobLauncher 설정
    .parametersExtractor(JobParametersExtractor)	// Step의 ExecutionContext를 Job이 실행되는데 필요한 JobParameters로 변환
    .build();						// JobStep을 생성
}
```
![image](https://github.com/haeyonghahn/spring-batch/assets/31242766/93659740-d6b8-4926-943e-272e496590c4)
![image](https://github.com/haeyonghahn/spring-batch/assets/31242766/dbeb27e4-6aa9-4def-8581-4ebe8efd8dd6)
![image](https://github.com/haeyonghahn/spring-batch/assets/31242766/0ad59d49-04aa-407d-8899-64de87f2e8a8)

## 스프링 배치 실행 - Flow
### FlowJob - 개념 및 API 소개
__기본개념__    
- Step 을 순차적으로만 구성하는 것이 아닌 특정한 상태에 따라 흐름을 전환하도록 구성할 수 있으며 FlowJobBuilder 에 의해 생성된다.
  - Step 이 실패 하더라도 Job 은 실패로 끝나지 않도록 해야 하는 경우
  - Step 이 성공 했을 때 다음에 실행해야 할 Step 을 구분해서 실행 해야 하는경우
  - 특정 Step 은 전혀 실행되지 않게 구성 해야 하는 경우
- Flow 와 Job 의 흐름을 구성하는데만 관여하고 실제 비즈니스 로직은 Step 에서 이루어진다.
- 내부적으로 SimpleFlow 객체를 포함하고 있으며 Job 실행 시 호출한다.

__SimpleJob vs FlowJob__     
![image](https://github.com/haeyonghahn/spring-batch/assets/31242766/8f321586-cf3d-4176-870b-fde0884c1759)

```java
public Job batchJob() {
  return jobBuilderFactory.get(“batchJob")
	  .start(Step)                            // Flow 시작하는 Step 설정
	  .on(String pattern)                     // Step의 실행 결과로 돌려받는 종료상태 (ExitStatus)를 캐치하여 매칭하는 패턴, TransitionBuilder 반환
	  .to(Step)                               // 다음으로 이동할 Step 지정
	  .stop()                                 // fail(), end(), stopAndRestart()	
	  .from(Step)                             // 이전 단계에서 정의한 Step의 Flow를 추가적으로 정의함
          .next(Step)                             // 다음으로 이동할 Step 지정
	  .end()                                  // build() 앞에 위치하면 FlowBuilder를 종료하고 SimpleFlow 객체 생성
	  .build()                                // FlowJob 생성하고 flow 필드에 SimpleFlow 저장
}
```
FlowBuilder 의 on(String pattern) 메서드를 호출하게 되면 TransitionBuilder 가 작동하게 되면서 Step 간 조건부 전환을 구성할 수 있게 된다.

![image](https://github.com/haeyonghahn/spring-batch/assets/31242766/8e64f4ae-9da3-47ff-a33c-5cd765c4602c)

### FlowJob - start() / next()
```java
public Job batchJob() {
  return jobBuilderFactory.get("batchJob")
    .start(Flow)	// 처음 실행할 Flow 설정, JobFlowBuilder가 반환된다. 여기에 Step이 인자로 오게 되면 SimpleJobBuilder가 반환
    .next(Step or Flow or JobExecutionDecider)
    .on(String pattern)
    .to(Step)
    .stop() / fail() / end() / stopAndRestart()
    .end()
    .build();
}
```
![image](https://github.com/haeyonghahn/spring-batch/assets/31242766/99dda99a-97cf-4679-a31b-fee1d23fee7f)

위의 구성은 Step을 Flow 별로 분리하여 구성하는 장점이 있지만 Step 중 하나라도 실패할 경우 전체 Job이 실패하는 규칙은 동일하다.

## 스프링 배치 청크 프로세스 이해
### Chunk
__기본 개념__   
- Chunk 란 여러 개의 아이템을 묶은 하나의 덩어리, 블록을 의미
- 한번에 하나씩 아이템을 입력 받아 Chunk 단위의 덩어리로 만든 후 Chunk 단위로 트랜잭션을 처리함, 즉 Chunk 단위의 Commit 과 Rollback 이 이루어짐
- 일반적으로 대용량 데이터를 한번에 처리하는 것이 아닌 청크 단위로 쪼개어서 더 이상 처리할 데이터가 없을 때까지 반복해서 입출력하는데 사용됨

![image](https://user-images.githubusercontent.com/31242766/230384461-ef90ae7c-0c6c-4b66-bf24-4be7b3fdc579.png)

- `Chunk<I>` vs `Chunk<O>`
  - `Chunk<I>` 는 ItemReader 로 읽은 하나의 아이템을 Chunk 에서 정한 개수만큼 반복해서 저장하는 타입
  - `Chunk<O>` 는 ItemReader 로부터 전달받은 `Chunk<I>` 를 참조해서 ItemProcessor 에서 적절하게 가공, 필터링한 다음 ItemWriter 에 전달하는 타입
  
![image](https://user-images.githubusercontent.com/31242766/230384677-4db69628-3189-43c1-a64e-a286f0911e54.png)
![image](https://user-images.githubusercontent.com/31242766/230387123-b5972889-4397-447b-b611-34af5dbad8d9.png)
![image](https://user-images.githubusercontent.com/31242766/230387424-7fb3b15a-e95a-41dd-8c11-faa6b1e9b86b.png)

### ChunkOrientedTasklet
__기본 개념__   
- ChunkOrientedTasklet 은 스프링 배치에서 제공하는 Tasklet 의 구현체로서 Chunk 지향 프로세싱를 담당하는 도메인 객체
- ItemReader, ItemWriter, ItemProcessor 를 사용해 Chunk 기반의 데이터 입출력 처리를 담당한다.
- TaskletStep 에 의해서 반복적으로 실행되며 ChunkOrientedTasklet 이 실행 될 때마다 매번 새로운 트랜잭션이 생성되어 처리가 이루어진다.
- exception이 발생할 경우, 해당 Chunk는 롤백 되며 이전에 커밋한 Chunk는 완료된 상태가 유지된다.
- 내부적으로 ItemReader 를 핸들링 하는 ChunkProvider 와 ItemProcessor, ItemWriter 를 핸들링하는 ChunkProcessor 타입의 구현체를 가진다.

__구조__   
![image](https://user-images.githubusercontent.com/31242766/230507134-b6bbcf85-19be-40ba-a2e3-e080ec1624a2.png)
![image](https://user-images.githubusercontent.com/31242766/230507208-194e1fc1-8f7f-4bc1-baa7-2c660223e723.png)
![image](https://user-images.githubusercontent.com/31242766/230507344-521c9ece-3f80-479e-9daa-c2ac148c76af.png)

![image](https://user-images.githubusercontent.com/31242766/230507497-b15898ba-b2f8-429d-8065-37f28593fc74.png)
![image](https://user-images.githubusercontent.com/31242766/230630361-e1947edb-5327-469c-a914-9fa1e1cc86bc.png)

### ChunkProvider
__기본 개념__   
- ItemReader 를 사용해서 소스로부터 아이템을 Chunk size 만큼 읽어서 Chunk 단위로 만들어 제공하는 도메인 객체이다.
- `Chunk<I>` 를 만들고 내부적으로 반복문을 사용해서 ItemReader.read() 를 계속 호출하면서 item 을 Chunk 에 쌓는다.
- 외부로 부터 ChunkProvider 가 호출될 때마다 항상 새로운 Chunk 가 생성된다.
- 반복문 종료 시점
  - Chunk size 만큼 item 을 읽으면 반복문 종료되고 ChunkProcessor 로 넘어간다.
  - ItemReader 가 읽은 item 이 null 일 경우 반복문 종료 및 해당 Step 반복문까지 종료된다.
  - 기본 구현체로서 SimpleChunkProvider 와 FaultTolerantChunkProvider 가 있다.

![image](https://user-images.githubusercontent.com/31242766/230769116-193abf12-6611-48c0-a133-645de830a735.png)

### ChunkProcessor
__기본 개념__   
- ItemProcessor 를 사용해서 Item 을 변형, 가공, 필터링하고 ItemWriter 를 사용해서 Chunk 데이터를 저장, 출력한다.
- `chunk<O>` 를 만들고 앞에서 넘어온 `Chunk<I>` 의 item 을 한 건씩 처리한 후 `Chunk<O>` 에 저장한다
- 외부로 부터 ChunkProcessor 가 호출될 때마다 항상 새로운 Chunk 가 생성된다.
- ItemProcessor 는 설정 시 선택사항으로서 만약 객체가 존재하지 않을 경우 ItemReader 에서 읽은 item 그대로가 `Chunk<O>` 에 저장된다.
- ItemProcessor 처리가 완료되면 Chunk<O> 에 있는 List<Item> 을 ItemWriter 에게 전달한다.
- ItemWriter 처리가 완료되면 Chunk 트랜잭션이 종료하게 되고 Step 반복문에서 ChunkOrientedTasklet 가 새롭게 실행된다.
- ItemWriter 는 Chunk size 만큼 데이터를 Commit 처리 하기 때문에 Chunk size 는 곧 Commit Interval 이 된다.
- 기본 구현체로서 SimpleChunkProcessor 와 FaultTolerantChunkProcessor 가 있다.

__구조__     
![image](https://user-images.githubusercontent.com/31242766/230898781-cf9dbefa-f44f-4062-8337-563e341d69a9.png)

![image](https://user-images.githubusercontent.com/31242766/230898875-19c2beb9-01db-435b-8527-fcbeb32a955c.png)

### ItemReader
__기본 개념__    
- 다양한 입력으로부터 데이터를 읽어서 제공하는 인터페이스
  - 플랫(Flat) 파일 – csv, txt (고정 위치로 정의된 데이터 필드나 특수문자로 구별된 데이터의 행)
  - XML, Json
  - Database
  - JMS, RabbitMQ 와 같은 Messag Queuing 서비스
  - Custom Reader - 구현 시 멀티 스레드 환경에서 스레드에 안전하게 구현할 필요가 있음
- ChunkOrientedTasklet 실행 시 필수적 요소로 설정해야 한다.

__구조__   
![image](https://user-images.githubusercontent.com/31242766/230923523-1df30da2-89ff-4d0a-9f28-b33be629a33a.png)

__T read()__   
- 입력 데이터를 읽고 다음 데이터로 이동한다.
- 아이템 하나를 리턴하며 더 이상 아이템이 없는 경우 null 리턴.
- 아이템 하나는 파일의 한줄, DB 의 한 row 혹은 XML 파일에서 하나의 엘리먼트가 될 수 있다.
- 더 이상 처리해야 할 Item 이 없어도 예외가 발생하지 않고 ItemProcessor 와 같은 다음 단계로 넘어 간다.

![image](https://user-images.githubusercontent.com/31242766/230928920-57b57655-5340-45f1-b93b-e9cd95d75a30.png)    

- 다수의 구현체들이 ItemReader 와 ItemStream 인터페이스를 동시에 구현하고 있음
  - 파일의 스트림을 열거나 종료, DB 커넥션을 열거나 종료, 입력 장치 초기화 등의 작업
  - ExecutionContext 에 read 와 관련된 여러가지 상태 정보를 저장해서 재시작 시 다시 참조 하도록 지원
- 일부를 제외하고 하위 클래스들은 기본적으로 스레드에 안전하지 않기 때문에 병렬 처리시 데이터 정합성을 위한 동기화 처리 필요

![image](https://user-images.githubusercontent.com/31242766/231192463-a2c05aaf-347c-4d2c-a94e-37ddcefb4212.png)

### ItemWriter
__기본 개념__   
- Chunk 단위로 데이터를 받아 일괄 출력 작업을 위한 인터페이스
  - 플랫(Flat) 파일 – csv, txt
  - XML, Json
  - Database
  - JMS, RabbitMQ 와 같은 Messag Queuing 서비스
  - Mail Service
  - Custom Writer
- 아이템 하나가 아닌 아이템 리스트를 전달 받는다. 
- ChunkOrientedTasklet 실행 시 필수적 요소로 설정해야 한다.

__구조__   
![image](https://user-images.githubusercontent.com/31242766/231194039-54f24b85-75bb-4d92-8a00-9f74dab65677.png)

- void write(List<? extends T> items)
  - 출력 데이터를 아이템 리스트로 받아 처리한다.
  - 출력이 완료되고 트랜잭션이 종료되면 새로운 Chunk 단위 프로세스로 이동한다.

![image](https://user-images.githubusercontent.com/31242766/231207039-31cfc7cb-3cc3-469b-9539-bd34614e0827.png)

- 다수의 구현체들이 ItemWriter 와 ItemStream 을 동시에 구현하고 있다.
  - 파일의 스트림을 열거나 종료, DB 커넥션을 열거나 종료, 출력 장치 초기화 등의 작업
- 보통 ItemReader 구현체와 1:1 대응 관계인 구현체들로 구성되어 있다.

![image](https://user-images.githubusercontent.com/31242766/232785648-6f63ccfa-cd14-45a9-bf4e-e188a9e4dc84.png)

### ItemProcessor
__기본 개념__
- 데이터를 출력하기 전에 데이터를 가공, 변형, 필터링하는 역할
- ItemReader 및 ItemWriter 와 분리되어 비즈니스 로직을 구현할 수 있다.
- ItemReader 로 부터 받은 아이템을 특정 타입으로 변환해서 ItemWriter에 넘겨 줄 수 있다.
- ItemReader 로 부터 받은 아이템들 중 필터과정을 거쳐 원하는 아이템들만 ItemWriter 에게 넘겨줄 수 있다.
  - ItemProcessor 에서 process() 실행결과 null을 반환하면 Chunk<O> 에 저장되지 않기 때문에 결국 ItemWriter에 전달되지 않는다
- ChunkOrientedTasklet 실행 시 선택적 요소이기 때문에 청크 기반 프로세싱에서 ItemProcessor 단계가 반드시 필요한 것은 아니다.

__구조__   
![image](https://user-images.githubusercontent.com/31242766/233120221-d6a08d51-74c0-44e5-8a29-445606ccc0eb.png)

- O process
  - `<I>` 제네릭은 ItemReader에서 받을 데이터 타입 지정
  - `<O>` 제네릭은 ItemWriter 에게 보낼 데이터 타입 지정
  - 아이템 하나씩 가공 처리하며 null 리턴할 경우 해당 아이템은 `Chunk<O>` 에 저장되지 않음

![image](https://user-images.githubusercontent.com/31242766/233120485-32a4be82-3fca-47ff-b128-e3b7c81e4c7c.png)
- ItemStream 을 구현하지 않는다.
- 거의 대부분 Customizing 해서 사용하기 때문에 기본적으로 제공되는 구현체가 적다.

![image](https://user-images.githubusercontent.com/31242766/233120653-9effbba4-fc8b-4674-9307-92172d043efb.png)

### ItemStream
__기본 개념__   
- ItemReader 와 ItemWriter 처리 과정 중 상태를 저장하고 오류가 발생하면 해당 상태를 참조하여 실패한 곳에서 재 시작 하도록 지원
- 리소스를 열고(open) 닫아야(close) 하며 입출력 장치 초기화 등의 작업을 해야 하는 경우
- ExecutionContext 를 매개변수로 받아서 상태 정보를 업데이트(update) 한다
- ItemReader 및 ItemWriter 는 ItemStream 을 구현해야 한다.

__구조__    
![image](https://user-images.githubusercontent.com/31242766/234208999-3be78f45-0b81-41b1-9a5d-8dc2b4fe3a30.png)

![image](https://user-images.githubusercontent.com/31242766/234209335-37896e78-a271-4dba-bc0a-0b1ee1464cfb.png)

### Chunk Process 아키텍처
![image](https://user-images.githubusercontent.com/31242766/234240577-53f8f743-3132-456e-bdf6-1010c4c18047.png)
![image](https://user-images.githubusercontent.com/31242766/234240639-04f0eba8-347d-45ba-b56a-7da26ad724c5.png)

## 스프링 배치 청크 프로세스 활용 - ItemReader
### FlatFileItemReader - 개념 및 API 소개
__기본 개념__   
- 2차원 데이터(표)로 표현된 유형의 파일을 처리하는 ItemReader
- 일반적으로 고정 위치로 정의된 데이터 필드나 특수 문자에 의해 구별된 데이터의 행을 읽는다.
- Resource 와 LineMapper 두 가지 요소가 필요하다.

__구조__    
![image](https://user-images.githubusercontent.com/31242766/234244335-4b988cf9-1c16-4e3a-aecf-3f5598e63af1.png)

- Resource
  - FileSystemResource – new FileSystemResource("resource/path/config.xml")
  - ClassPathResource – new ClassPathResource("classpath:path/config.xml")

- LineMapper
  - 파일의 라인 한줄을 Object 로 변환해서 FlatFileItemReader 로 리턴한다.
  - 단순히 문자열을 받기 때문에 문자열을 토큰화해서 객체로 매핑하는 과정이 필요하다.
  - LineTokenizer 와 FieldSetMapper 를 사용해서 처리한다.
  - FieldSet
    - 라인을 필드로 구분해서 만든 배열 토큰을 전달하면 토큰 필드를 참조 할수 있도록 한다
    - JDBC 의 ResultSet 과 유사하다 ex) fs.readString(0), fs.readString("name")
  - LineTokenizer
    - 입력받은 라인을 FieldSet 으로 변환해서 리턴한다.
    - 파일마다 형식이 다르기 때문에 문자열을 FieldSet 으로 변환하는 작업을 추상화시켜야 한다. 
  - FieldSetMapper
    - FieldSet 객체를 받아서 원하는 객체로 매핑해서 리턴한다.
    - JdbcTemplate 의 RowMapper 와 동일한 패턴을 사용한다.

![image](https://user-images.githubusercontent.com/31242766/234246374-0f5498d2-015d-4ace-804a-82dde87708d9.png)
![image](https://user-images.githubusercontent.com/31242766/234246624-20ec6c98-7f46-48de-a669-690f573518df.png)

```java
public FlatFileItemReader itemReader() {
  return new FlatFileItemReaderBuilder<T>()
    .name(String name)                  // 이름 설정, ExecutionContext 내에서 구분하기 위한 key 로 저장
    .resource(Resource)	                // 읽어야 할 리소스 설정
    .delimited().delimiter("|")         // 파일의 구분자를 기준으로 파일을 읽어들이는 설정
    .fixedLength()                      // 파일의 고정길이를 기준으로 파일을 읽어들이는 설정
    .addColumns(Range..)                // 고정길이 범위를 정하는 설정
    .names(String[] fieldNames)         // LineTokenizer 로 구분된 라인의 항목을 객체의 필드명과 매핑하도록 설정
    .targetType(Class class)            // 라인의 각 항목과 매핑할 객체 타입 설정
    .addComment(String Comment)         // 무시할 라인의 코멘트 기호 설정
    .strict(boolean)                    // 라인을 읽거나 토큰화 할 때 Parsing 예외가 발생하지 않도록 검증 생략하도록 설정
    .encoding(String encoding)          // 파일 인코딩 설정
    .linesToSkip(int linesToSkip)       // 파일 상단에 있는 무시할 라인 수 설정
    .saveState(boolean)                 // 상태정보를 저장할 것인지 설정
    .setLineMapper(LineMapper)          // LineMapper 객체 설정
    .setFieldSetMapper(FieldSetMapper)  // FieldSetMapper 객체 설정
    .setLineTokenizer(LineTokenizer)    // LineTokenizer 객체 설정
    .build();
}
```
### FlatFileItemReader - delimetedlinetokenizer
__기본 개념__    
- 한 개 라인의 String을 구분자 기준으로 나누어 토큰화하는 방식

__구조__   
![image](https://github.com/haeyonghahn/spring-batch/assets/31242766/ff600c04-167e-47e4-bbc7-7050405b3e5f)

### FlatFileItemReader - fixedlengthtokenizer
__기본 개념__   
- 한 개 라인의 String을 사용자가 설정한 고정길이 기준으로 나누어 토큰화하는 방식
- 범위는 문자열 형식으로 설정할 수 있다.
  - "1-4" 또는 "1-3,4-6,7" 또는 "1-2,4-5,7-10"
  - 마지막 범위가 열려 있으면 나머지 행이 해당 열로 읽혀진다.

__구조__   
![image](https://github.com/haeyonghahn/spring-batch/assets/31242766/c0f6c793-3302-454c-a6cb-bc5761f2ec13)

### FlatFileItemReader - Exception Handling
__기본 개념__   
- 라인을 읽거나 토큰화할 때 발생하는 Parsing 예외를 처리할 수 있도록 예외 계층 제공
- 토큰화 검증을 엄격하게 적용하지 않도록 설정하면 Parsing 예외가 발생하지 않도록 할 수 있다.

![image](https://github.com/haeyonghahn/spring-batch/assets/31242766/349a234e-198f-4b32-9704-a7f9ead0bf8b)

__토큰화 검증 기준 설정__   
```java
tokenizer.setColumns(new Range[] { new Range(1, 5), new Range(6, 10) }); // 토큰 길이 : 10자
tokenizer.setStrict(false); // 토큰화 검증을 적용하지 않음
FieldSet tokens = tokenizer.tokenize("12345"); // 라인 길이 : 5자
```
- LineTokenizer의 Strict 속성을 `false`로 설정하게 되면 Tokenizer가 라인 길이를 검증하지 않는다.
- Tokenizer가 라인 길이나 컬럼명을 검증하지 않을 경우 예외가 발생하지 않는다.
- FieldSet은 성공적으로 리턴이 되며 두번째 범위 값은 빈 토큰을 가지게 된다.

### JsonItemReader
__기본 개념__   
- Json 데이터의 Parsing과 Binding을 JsonObjectReader 인터페이스 구현체에 위임하여 처리하는 ItemReader
- 두 가지 구현체 제공
  - JacksonJsonObjectReader
  - GsonJsonObjectReader
 
__구조__     
![image](https://github.com/haeyonghahn/spring-batch/assets/31242766/e9487b8c-3cdc-4f9d-9978-1dabe0ebbc1f)
![image](https://github.com/haeyonghahn/spring-batch/assets/31242766/b813a6bd-4e6d-4dda-a9db-fa00500a4de1)

__API 설정__    
![image](https://github.com/haeyonghahn/spring-batch/assets/31242766/1f744a79-68b9-45ec-a22e-cf4ad07fbd2b)

### DB - Cursor & Paging 이해
__기본 개념__   
- 배치 어플리케이션은 실시간적 처리가 어려운 대용량 데이터를 다루며 이 때 DB I/O의 성능문제와 메모리 자원의 효율성 문제를 해결할 수 있어야 한다.
- 스프링 배치에서는 대용량 데이터 처리를 위한 두 가지 해결방안을 제시하고 있다.

__Cursor Based 처리__   
- JDBC ResultSet의 기본 메커니즘을 사용
- 현재 행에 커서를 유지하며 다음 데이터를 호출하면 다음 행으로 커서를 이동하며 데이터 반환이 이루어지는 Streaming 방식의 I/O이다.
- ResultSet이 open될 때마다 next() 메소드가 호출되어 Database의 데이터가 반환되고 객체와 매핑이 이루어진다.
- DB Connection이 연결되면 배치 처리가 완료될 때까지 데이터를 읽어오기 때문에 DB와 SocketTimeout을 충분히 큰 값으로 설정 필요
- 모든 결과를 메모리에 할당하기 때문에 메모리 사용량이 많아지는 단점이 있다.
- Connection 연결 유지 시간과 메모리 공간이 충분하다면 대량의 데이터 처리에 적합할 수 있다 (fetchSize 조절)

__Paging Based 처리__   
- 페이징 단위로 데이터를 조회하는 방식으로 Page Size만큼 한번에 메모리로 가지고 온 다음 한 개씩 읽는다.
- 한 페이지를 읽을때마다 Connection을 맺고 끊기 때문에 대량의 데이터를 처리하더라도 SocketTimeout 예외가 거의 일어나지 않는다.
- 시작 행 번호를 지정하고 페이지에 반환시키고자 하는 행의 수를 지정한 후 사용 - Offset, Limit
- 페이징 단위의 결과만 메모리에 할당하기 때문에 메모리 사용량이 적어지는 장점이 있다.
- Connection 연결 유지 시간이 길지 않고 메모리 공간을 효율적으로 사용해야 하는 데이터 처리에 적합할 수 있다.

![image](https://github.com/haeyonghahn/spring-batch/assets/31242766/203e467f-29a3-4a13-93d3-b85022c440ba)

### DB - JdbcCursorItemReader
__기본 개념__   
- Cursor 기반의 JDBC 구현체로서 ResultSet과 함께 사용되며 Datasource에서 Connection을 얻어와서 SQL을 실행한다.
- Thread 안정성을 보장하지 않기 때문에 멀티 스레드 환경에서 사용할 경우 동시성 이슈가 발생하지 않도록 별도 동기화 처리가 필요하다.

__API__   
```java
public JdbcCursorItemReader itemReader() {
        return new JdbcCursorItemReaderBuilder<T>()
	.name(“cursorItemReader")
 	.fetchSize(int chunkSize)			// Cursor 방식으로 데이터를 가지고 올 때 한번에 메모리에 할당한 크기를 설정한다. Chunck Size와 동일하게 준다.
	.dataSource(DataSource)				// DB에 접근하기 위해 Datasource 설정
	.rowMapper(RowMapper)				// 쿼리 결과로 반환되는 데이터와 객체를 매핑하기 위한 RowMapper 설정
	.beanRowMapper(Class<T>)			// 별도의 RowMapper를 설정하지 않고 클래스 타입을 설정하면 자동으로 객체와 매핑
	.sql(String sql)				// ItemReader가 조회할 때 사용할 쿼리 문장 설정
	.queryArguments(Object... args)			// 쿼리 파라미터 설정
	.maxItemCount(int count)			// 조회할 최대 item 수
	.currentItemCount(int count)			// 조회 Item의 시작 지점
	.maxRows(int maxRows)				// ResultSet 오브젝트가 포함할 수 있는 최대 행 수
	.build();
    }
```
![image](https://github.com/haeyonghahn/spring-batch/assets/31242766/13b4e889-3d6a-403c-94c7-7fc4678dd449)

### DB - JpaCursorItemReader

## 스프링 배치 테스트 및 운영
### Spring Batch Test
__pom.xml__   
```xml
<dependency>
	<groupId>org.springframework.batch</groupId>
	<artifactId>spring-batch-test</artifactId>	
</dependency>
```

__@SpringBatchTest__   
- 자동으로 ApplicationContext에 테스트에 필요한 여러 유틸 Bean을 등록해 주는 어노테이션
  - JobLauncherTestUtils
    - `launchJob()`, `launchStep()` 과 같은 스프링 배치 테스트에 필요한 유틸성 메소드 지원
  - JobRepositoryTestUtils
    - JobRepository를 사용해서 JobExecution을 생성 및 삭제 기능 메소드 지원
  - StepScopeTestExecutionListener
    - `@StepScope` 컨텍스트를 생성해주며 해당 컨텍스트를 통해 JobParameter 등을 단위 테스트에서 DI 받을 수 있다.
  - JobScopeTestExecutionListener
    - `@JobScope` 컨텍스트를 생성해주며 해당 컨텍스트를 통해 JobParameter 등을 단위 테스트에서 DI 받을 수 있다.

![image](https://github.com/haeyonghahn/spring-batch/assets/31242766/193b9aa6-7030-469b-994f-a2c73c1aad78)

```java
@RunWith(SpringRunner.class)
@SpringBatchTest
@SpringBootTest(classes={BatchJobConfiguration.class, TestBatchConfig.class})
public class BatchJobConfigurationTest {
    ...
}
```
- `@SpringBatchTest` : JobLauncherTestUtils, JobRepositoryTestUtils 등을 제공하는 어노테이션
- `@SpringBootTest(classes={...})` : Job 설정 클래스 지정, 통합 테스트를 위한 여러 의존성 빈들을 주입 받기 위한 어노테이션

```java
@Configuration
@EnableAutoConfiguration
@EnableBatchProcessing
public class TestBatchConfig {}
```
- `@EnableBatchProcessing` : 테스트 시 배치환경 및 설정 초기화를 자동 구동하기 위한 어노테이션
- 테스트 클래스마다 선언하지 않고 공통으로 사용하기 위함

### JobExplorer / JobRegistry / JobOperator
- JobExplorer
  - JobRepository의 readonly 버전
  - 실행 중인 Job의 실행 정보인 JobExecution 또는 Step의 실행 정보인 StepExecution을 조회할 수 있다.
- JobRegistry
  - 생성된 Job을 자동으로 등록, 추적 및 관리하며 여러 곳에서 job을 생성한 경우 ApplicationContext에서 job을 수집해서 사용할 수 있다.
  - 기본 구현체로 map 기반의 MapJobRegistry 클래스를 제공한다.
    - jobName을 key로 하고 job을 값으로 하여 매핑한다.
  - job 등록
    - `JobRegistryBeanPostProcessor` `BeanPostProcessor` 단계에서 bean 초기화 시 자동으로 JobRegistry에 job을 등록시켜준다.
- JobOperator
  - JobExplorer, JobRepository, JobRegistry, JobLauncher를 포함하고 있으며 배치의 중단, 재시작, job 요약 등의 모니터링이 가능하다.
  - 기본 구현체로 SimpleJobOperator 클래스를 제공한다.

![image](https://github.com/haeyonghahn/spring-batch/assets/31242766/aade4c8f-9eaf-4172-b117-cba8c19502ea)
![image](https://github.com/haeyonghahn/spring-batch/assets/31242766/90740aeb-1d19-42a2-abc8-786b0e554cb2)
![image](https://github.com/haeyonghahn/spring-batch/assets/31242766/c329e7a1-9751-403e-9899-29bffa500074)

