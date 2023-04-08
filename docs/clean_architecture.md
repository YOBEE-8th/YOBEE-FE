# 클린 아키텍쳐 & Hilt

### **멀티 모듈**

- PresentLayer, DomainLayer, DataLayer를 모듈로 구분
---
<br>

### **PresentLayer**

> View와 ViewModel이 존재하며 MVVM 패턴을 적용하여 View와 Model 사이의 독립 시켰다
---
<br>

### **DomainLayer**

> UseCase, Repository(interface), model이 존재하며 비즈니스 로직을 처리하는 Layer

> DomainLayer에 작성되는 코드들은 순수 Kotlin코드로 다른 플랫폼 또는 프로젝트에 의존하지 않도록 작성했다.

- UseCase: 사용 하는 API 종류에 따라 구분하여 작성했다.
- Repository: 의존성 역전을 통해 DataLayer에 의존성을 갖지 않도록 구현했다.
- model: DomainLayer에서 처리되는 비즈니스 로직의 실질적 데이터
---
<br>

### **DataLayer**

> DataSource, APIService, Dependency Injection, mappers가 존재하며 로컬 데이터베이스, SharedPreference, 서버 데이터베이스에 접근하는 Layer.

- DataSource: APIService를 로컬 데이터 저장소(SharedPreference, local DB)와 같은 다른 데이터 소스와 독립시키기 위해 사용했다.

- APIService: 서버와 통신하는 코드(retrofit)이 작성됨.

- DI: Dagger Hilt를 사용하기 위한 Module들을 작성.
    - Module: 앱에서 사용할 객체를 생성하고, 객체의 의존성 주입을 처리하기 위한 제공자, 객체를 생성하기 위해 필요한 데이터 제공

- Mapper: ResponseDto를 DomainModel로 변환하기 위한 클래스를 작성
    ```
    참고자료에서는 DomainLayer에 존재하지만(translater) ResponseDto에 대해 의존성이 생길것을 염려해 DataLayer에 작성했다.
    
    이후에 알게 된 사실이지만 Mapper객체를 인터페이스 형태로 DomainLayer에서 작성하고 의존성 역전원칙을 적용하여 DataLayer에서 구현하는 것이 더 좋다고 한다. 
    ```
---
<br>

### **Dagger Hilt**

> 프로젝트 초기에는 익숙하지 않아서 @AndroidEntrypoint, @HiltViewModel 등 어노테이션을 달아주지 않아서 힐트가 인식하지 못했던 이슈가 빈번히 있었다.

> 또한 인터페이스에 대한 생성자를 Module에 Provide하는 코드를 작성하지 않아서 발생하는 에러도 꽤 있었다.

---
<br>

**참고 자료**: [https://meetup.nhncloud.com/posts/345](https://meetup.nhncloud.com/posts/345)