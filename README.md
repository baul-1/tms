# 웹툰 공정 관리 시스템 (Toon Manager System)

## 용어 정리

* TASK 
  * 에피소드의 공정 하나의 단위
  * ex) 화산 귀환(웹툰)  1화 에피소드의 밑색 공정
    
* WORK
  * WORK 는 TASK 의 종속되는 하나의 단위
  * ex)  화산 귀환(웹툰)  1화 에피소드의 밑색 공정에 1개의 WORK




## 기능 요약

유저는 크게 3가지가 있습니다. 회사, 팀장, 팀원 

* 회사
  * 오프라인적으로 웹툰을 계약(외주) or 자체 제작을 결정합니다.
  * 진행할 웹툰을 시스템 안에 생성합니다.
  * 진행할 에피소드를 생성합니다.    
  * 팀을 만듭니다.
  * 팀원을 만들고 팀에 배정합니다.
  * 웹툰 공정에 필요함 캐리터 및 캐릭터 스타일을 생성합니다.
    

* 팀장
  * TASK 를 생성합니다.
  * WORK 를 생성합니다. 시작하는 날짜는 생성하는 날짜 보다 커야 됩니다. 이유는 팀원이 하고 있는 스케줄이 있지만 무리해서 이걸 해야되는 상황이면 팀원은 기존 하는 스케줄도 못 맞출 수 있는 상황이 될 수 있음. 
    그래서 팀장과 팀원이 서로 합의를 한 상태에서 WORK 가 진행 되야 되므로 WORK 시작하는 날짜 하루 전에 WORK 를 생성 해야됩니다.
  * WORK 에 일할 팀원들을 배정합니다.
  * WORK 에 작업할 파일을 업로드를해서 줄수도 있고 시스템 이외에 전달 받은 파일을 작업해서 팀원이 WORK에 업로드 가능  
  * 팀장은 WORK를 생성할때 작업할 스타일을 배정해줄 수 있습니다. 단 WORK 의 상태가 진행이 되면 더 이상 추가 및 변경은 안됩니다. 
    이유는 작업 중인 팀원이 갑작스럽게 스타일 변경되면  기존 스타일로 작업을 했던게 필요가 없어짐으로 분쟁할 여지를 없애기 위해 상태가 진행 전까지만
    스타일을 변경 및 추가할 수 있습니다.
  * 팀장은 WORK 의 배정 된 팀원들의 결과 검토 요청, 종료일 변경 요청, 작업 취소 요청 등을 확인 후 컨펌을 해줘야됩니다.  
    


* 팀원
  * 배정 받은 WORK(일)을 확인하고 수락 및 거절을 할 수 있습니다.
  * 수락을 하고나면 시작일자가 오늘이면 30분 이네 진행 상태로 변경이 됩니다.
  * WORK 의 상태가 진행이 아니면 파일 업로드를 할 수 없습니다. 팀원이 보게 될 WORK 의 파일 업로드 공간은 workspace 라고 생각하시면 됩니다.
    업로드 할때마다 버전이 체크가 되고 팀장이 업로드를 한 파일을 볼 수 있습니다.
  * WORK 의 진행률 올릴 수 있습니다.
  * 배정 받은 WORK 가 끝났으면 팀장에게 결과를 보고 할 수 있습니다.
  * WORK 의 종료일을 변경을 팀장에게 보고 할 수 있습니다.  
    




#환경 설정

## Server
  * Spring boot 2.5.5
  

  * Webflux
  

  * Redis 
 
 
  * MariaDB
  

  * MariaDB DB4J  
  

  * java 17

## Front
  * JavaScript

## DB
  * Maridb, Redis 로컬에서 사용할 수 있게 설정해놨습니다. 다른 서버를 쓰실 경우 Application 설정만 바꾸시면됩니다.

  * resources 폴더 안에 init_db 에 DDL 문이 작성 되어 있으니깐 Trigger, Event 를 DB 에서 실행 하시면됩니다.


## Spring Application 설정 
  * 환견변수로 설정 되어 있는 값들은 IDE 에서 설정하시거나 정적으로 입력 하셔도됩니다.
  


### 예시  
```c

server.port=3000
server.compression.enabled=true
server.compression.mime-types=application/json,application/xml,text/html,text/xml,text/plain,application/javascript,text/css
server.compression.min-response-size=2KB

spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.datasource.url=jdbc:mysql:jdbc:mariadb://localhost:3309/tms
spring.datasource.username=root
spring.datasource.password=

mariaDB4j.dataDir=C:/embedded/db
mariaDB4j.port=3309
mariaDB4j.isMysqlActive=Y
mariaDB4j.host=localhost
mariaDB4j.initSchema=init_db/schema.sql
mariaDB4j.initData=init_db/data.sql


# jdbc:sqlite:C:/sqlite/tms.db;

spring.datasource.hikari.connectionTimeout=30000
spring.datasource.hikari.idleTimeout=600000
spring.datasource.hikari.maxLifetime=1800000

spring.redis.lettuce.pool.max-active=10
spring.redis.lettuce.pool.max-idle=10
spring.redis.lettuce.pool.min-idle=2

spring.redis.hostnotport=
spring.redis.port=

server.uploadroot=C/:A17

```

# CI / CD
 * GitLab - Runners 
 * Gitlab - Variables 변수 셋팅
