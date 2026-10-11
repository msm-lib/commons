# commons

Thư viện Java dùng chung cho các service MSM (package `com.msm.core`). Cung cấp:

- **Typesafe metadata**: khai báo cột/field có kiểu (`TypedAttribute`, `MetaFieldBuilder`, `DataRecord`).
- **Dynamic query** trên jOOQ: filter / lookup / insert / update / delete dựa theo metadata, có phân quyền theo data scope.
- **Action & hook engine**: định tuyến request theo `resource` + `action` tới handler, kèm hook before / after / after-commit.
- **Metadata object** build từ JPA metamodel và annotation.
- **Validation**, **security** (permission, data scope), **exception** chuẩn và các tiện ích (`Utils`).

> Thư viện **không phụ thuộc Spring** và không có auto-configuration. Service sử dụng phải tự tạo bean và đăng ký các hook tĩnh (xem [Khởi tạo](#khởi-tạo)). Với service Spring Boot, nên dùng [generic-object-management](https://github.com/msm-lib/generic-object-management): thư viện đó đã làm sẵn phần này.

## Cài đặt

Bản phát hành là **git tag** (ví dụ `2.3.7`), build qua [JitPack](https://jitpack.io/#msm-lib/commons). Dòng `version` trong `build.gradle.kts` không phải version được publish.

```kotlin
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.msm-lib:commons:2.3.7")
}
```

Yêu cầu: **Java 21**, **PostgreSQL** (cần extension `unaccent` cho toán tử `LIKE`/`CONTAINS`).

Thư viện khai báo các dependency dạng `implementation`. Nếu code của service dùng trực tiếp type của jOOQ (3.21.x), Jackson, QueryDSL (OpenFeign 6.10.x) hay Hibernate 6.6 thì phải tự khai báo các dependency đó, đúng version.

## Cấu trúc package

| Package | Nội dung |
|---|---|
| `action` | Action/hook engine: `@Handler`, `@CreateHandler`, `@HookBefore`…, `ActionContext`, `DefaultActionExecutor`, `DefaultHookEngine`, `TransactionUtils` |
| `commons` | Tiện ích `Utils.*`, `Constants`, `GenericTypeResolverFactory` (chuỗi kiểu → Jackson `JavaType`) |
| `dynamicquery` | CRUD jOOQ theo metadata: `DefaultFilterQuery`, `DefaultDynamicInsert/Update/Delete`, operator, `FieldResolver`, `ObjectMetadataFactory`, `JavaTypeMappingFactory` |
| `exceptions` | `GenericBaseException`, factory `CommonErrors`, `ErrorCodeEnum` |
| `filter` | DTO request/response dùng chung (`ObjectFilterRequest`, `PageResponse`…) và engine filter QueryDSL/JPA (`AdvancedFilterService`) |
| `metadata` | `ObjectMetadata`, `Attribute`, annotation (`@AttributeDefinition`, `@ObjectDefinitionRef`…), `typesafe` (`TypedAttribute`, `MetaFieldBuilder`, `DataRecord`), `mapper` (`RecordMapper`, `References`) |
| `security` | Data scope (OWNER, TEAM, BU, PARENT_CHILD, ORG), `RequestContextHolder`, `@SecuredField`, `@IgnorePermission` |
| `strategy` | `AbstractStrategyRegistry`: registry strategy theo key |
| `validate` | Validate attribute (required, kiểu, min/max/regex), rule MVEL |

## Khởi tạo

Thực hiện một lần khi ứng dụng khởi động:

```java
// 1. Build metadata từ JPA entity và đăng ký vào ObjectMetadataFactory
ObjectMetadataBuilder.buildFromMetamodel(entityManager.getMetamodel());

// 2. Cung cấp người dùng hiện tại (thường đọc từ ThreadLocal / SecurityContext)
RequestContextHolder.setRequestContextProvider(() -> currentRequestContext());

// 3. Nối hook after-commit với transaction manager
TransactionUtils.setHook(myTransactionHook);

// 4. Đăng ký package chứa kiểu tự định nghĩa dùng trong chuỗi kiểu
GenericTypeResolverFactory.registerPackage("com.mycompany.domain.*");

// 5. Tạo các bean query
var filterQuery = new DefaultFilterQuery(dsl, new SecurityConditionProvider(new DataScopeConditionResolver()));
var insert = new DefaultDynamicInsert(dsl);
var update = new DefaultDynamicUpdate(dsl);
var delete = new DefaultDynamicDelete(dsl, update);
```

Những điểm cần nhớ:

- Nếu không set `RequestContextProvider`, mặc định là user SYSTEM **không có quyền**, nên mọi query có security đều trả về **rỗng**.
- Nếu không set `TransactionUtils.setHook`, hook "after commit" chạy **ngay lập tức**, trước khi transaction commit.
- Thư viện **không tự scan** handler/hook. Service phải tự đăng ký vào `ActionHandlerFactory` và `HookDefinitionHandlerFactory`.
- Hàm `unaccent` cho Hibernate (`CustomUnaccent`) phải được đăng ký qua `META-INF/services/org.hibernate.boot.model.FunctionContributor`.
- Các registry tĩnh (operator, data type, rule, handler) **không thread-safe**, nên chỉ đăng ký lúc khởi động.

## Typesafe metadata

Mỗi bảng có một class `*Meta` khai báo `TABLE` và các `TypedAttribute`:

```java
import static com.msm.core.commons.GenericTypeResolverFactory.resolve;
import static com.msm.core.metadata.typesafe.MetaFieldBuilder.attr;

public final class OrderMeta {
    private OrderMeta() {}

    public static final String OBJECT_NAME = "order";
    public static final Table<?> TABLE = DSL.table(DSL.name("order"));

    public static final TypedAttribute<UUID> ID =
            attr(TABLE, "id", "id", UUID.class);
    public static final TypedAttribute<String> ORDER_CODE =
            attr(TABLE, "orderCode", "order_code", String.class);
    public static final TypedAttribute<Map<String, Object>> CUSTOM_VALUES =
            attr(TABLE, "customValues", "custom_values", resolve(Map.class, String.class, Object.class));
    // Field không gắn cột (DTO, nội dung JSON, reference)
    public static final TypedAttribute<Map<String, Object>> SHIP_TO_ACCOUNT_SITE =
            attr("shipToAccountSite", resolve(Map.class, String.class, Object.class));
}
```

### Chọn overload `MetaFieldBuilder`

| Trường hợp | Cách viết |
|---|---|
| Cột kiểu đơn giản | `attr(TABLE, "camelField", "snake_column", Type.class)` |
| Cột cần kiểu jOOQ riêng (array, UUID[], converter) | `attr(TABLE, field, column, DataType<T>)` |
| Cột JSONB kiểu generic (`Map`, `List<Foo>`) | `attr(TABLE, field, column, resolve(...))` |
| Field không gắn cột (DTO, nội dung JSON) | `attr("field", Type.class)` / `attr("field", JavaType)` |
| Giá trị nằm trong JSONB `custom_values` | `attrRef(TABLE, refName, Type.class)` / `attrRef(TABLE, refName, JavaType)` |

Field không gắn cột có `getField() == null`, vì vậy **không được** dùng nó trong câu SQL.

Kiểu generic có thể tạo bằng `resolve(List.class, UUID.class)`, `resolve(Map.class, String.class, Object.class)` hoặc từ chuỗi `resolve("List<UUID>")`. Chuỗi kiểu mặc định chỉ nhận các package `java.lang`, `java.util`, `java.math`, `java.text`, `java.time`. Với kiểu khác, dùng FQCN hoặc gọi `registerPackage`.

### Quy ước đặt tên

- **Field** (key trong payload/map) viết `camelCase`; **cột** DB viết `snake_case`.
- **Tên object** (`OBJECT_NAME`, `resource`) viết thường, liền nhau (`importjob`, `v2blanketorder`). `ObjectMetadataFactory` phân biệt hoa thường.
- Các field có tên cố định:

  | Ý nghĩa | Field |
  |---|---|
  | Khóa chính | `id` |
  | Khóa lạc quan | `version` |
  | Cờ xóa mềm | `isDeleted` |
  | Audit | `createdAt/By/ById`, `updatedAt/By/ById`, `deletedAt/By/ById` |
  | Team (data scope) | `teamId` |

- **Thuộc tính động** lưu trong cột JSONB `custom_values`, tương ứng với field `customValues`.
- **Reference** của field `x` lưu trong `xReference` (JSON chỉ chứa các field khai báo, mặc định là `id`, `code`, `name`). Nên tạo reference bằng `References` hoặc `RecordMapper` (khai báo `static final`).

### `DataRecord`

```java
DataRecord record = DataRecord.copyOf(map);      // copy, không ảnh hưởng map gốc
String code = record.get(OrderMeta.ORDER_CODE);
record.set(OrderMeta.ORDER_CODE, "SO001");
```

`DataRecord.of(map)` **không copy**: ghi vào record sẽ sửa luôn map gốc, và lỗi nếu map gốc immutable (`Map.of`).

### Build metadata từ JPA

- **Tên object**: entity name viết thường.
- **Tên bảng**: lấy từ `@Table(name)`.
- **Tên cột**: lấy theo thứ tự `@AttributeDefinition.columnName`, rồi `@Column(name)`, rồi đến **tên field Java**. Vì vậy luôn khai báo `@Column(name = "snake_case")`.
- **Required**: `@AttributeDefinition(required = ...)` được ưu tiên trước `@Column(nullable = false)` và `@NotNull`.
- **Annotation thường dùng**:

  | Annotation | Dùng để |
  |---|---|
  | `@AttributeDefinition(..., attributeRef = @AttributeDefinitionRef(...))` | Cấu hình attribute và reference |
  | `@ObjectDefinitionRef(relationType = CHILD_OF)` | Object con kế thừa quyền của object cha |
  | `@IgnorePermission` | Tắt security cho object |
  | `@SecuredField(TEAM)` | Đánh dấu cột dùng cho data scope |

## Dynamic query

### Request filter

```json
{
  "returnFields": ["id", "orderCode", "status"],
  "search": { "keyword": "abc", "fields": ["orderCode", "name"] },
  "filters": {
    "operator": "AND",
    "conditions": [
      { "field": "status", "operator": "IN", "value": ["NEW", "APPROVED"] },
      { "field": "createdAt", "operator": "BETWEEN", "value": ["2026-01-01T00:00:00Z", "2026-02-01T00:00:00Z"] },
      { "operator": "OR", "conditions": [ { "field": "teamId", "operator": "EQUALS", "value": null } ] }
    ]
  },
  "pageRequest": { "page": 1, "size": 20, "sorts": [ { "attribute": "createdAt", "direction": "DESC" } ] }
}
```

- `page` **bắt đầu từ 1**. Tổng số bản ghi chỉ được tính khi có `pageRequest`.
- Chỉ filter, sort hoặc select được các field **có trong metadata**. Field lạ ném `FieldNotFoundException`.
- Field JSON dùng được đường dẫn `a.b.c`. Giá trị lấy ra là **text**, nên `GREATER_THAN`/`LESS_THAN` so sánh theo chuỗi; chỉ `BETWEEN` có ép kiểu.
- `filter` và `lookup` tự động:
  - thêm `(isDeleted = false OR isDeleted IS NULL)` nếu object có `isDeleted` và request chưa filter field này;
  - thêm điều kiện data scope của user.

### Toán tử

| Toán tử | Ghi chú |
|---|---|
| `EQUALS`, `NOT_EQUALS` | `EQUALS` với `null` thành `IS NULL` |
| `LIKE` | `unaccent(lower(field)) LIKE unaccent('%keyword%')`. `%` và `_` trong từ khóa **không** được escape. Cột không phải chuỗi bị bỏ qua |
| `GREATER_THAN(_OR_EQUAL)`, `LESS_THAN(_OR_EQUAL)` | |
| `IN`, `NOT_IN` | `value` phải là mảng |
| `BETWEEN` | `value` là mảng đúng 2 phần tử |
| `CONTAINS` | Giá trị đơn: LIKE không dấu. Mảng: `@>` trên cột array |
| `CONTAINS_ONE_OF` | `&&` trên cột array |
| `NOT_CONTAINS`, `NOT_CONTAINS_ONE_OF` | **Chưa hỗ trợ** (ném `UnsupportedException`) |

### Ghi dữ liệu

- `update*` và `delete*` **bắt buộc có điều kiện where**.
- **Khóa lạc quan:** nếu object có field `version`, payload update **phải có `version` dạng số**. Thư viện ghi `version + 1` với điều kiện `WHERE version = <cũ>`. Không có dòng nào bị ảnh hưởng thì ném `OptimisticLockingFailureException`.
- **Xóa:** `delete*` là **xóa mềm** (thực chất là update), nên bạn phải tự truyền `isDeleted`, `deletedAt`… Muốn xóa thật thì dùng `forceDelete*`.
- **Insert:** bỏ qua các giá trị `null`. Insert/update hàng loạt được chia lô 1000 bản ghi.
- **Payload:** key không có trong metadata bị **bỏ qua mà không báo lỗi**.
- **Không tự động:** thao tác ghi **không** kiểm tra data scope (tự gọi `SecurityCheckProvider.checkDataScope`) và **không** tự điền field audit.

## Action & hook

```java
@Handler(resource = "order", action = "approve")
public Map<String, Object> approve(ActionContext<Map<String, Object>> ctx) { ... }

@HookBefore(resource = "order", action = Constants.Action.CREATE, order = 10)
public void beforeCreate(ActionContext<Map<String, Object>> ctx) { ... }
```

- Key định tuyến (viết thường):

  | Loại | Key |
  |---|---|
  | Handler | `handler:<resource>:<action>` |
  | Hook | `hook:<resource>:<action>:<PHASE>` |

- Không tìm thấy handler riêng thì dùng handler `resource = "*"`. Có nhiều handler cùng khớp thì ném `IllegalStateException`.
- Hook riêng của một resource **thay thế** hook `*`, không gộp chung.
- Engine **không sắp xếp theo `order`**, nên phải đăng ký đúng thứ tự.
- Hook after-commit chạy trên thread khác, nên **không** có `RequestContext` (ThreadLocal). Lỗi trong hook này chỉ được ghi log.

## Exception

- **Tạo lỗi:** dùng factory `CommonErrors` (`required`, `invalid`, `objectNotFound`, `fieldNotFoundException`, `optimisticLockingFailureException`…). Các exception đều kế thừa `GenericBaseException` (unchecked), mang `ErrorCode` và map `params`.
- **Stack trace:** exception **không có stack trace** (đã override `fillInStackTrace`). Khi bọc lỗi khác, nhớ log kèm cause.
- **Mã lỗi riêng:** implement interface `ErrorCode`, đặt tên dạng `UPPER_SNAKE_CASE`.
- **i18n:** chưa có. Message validate là tiếng Việt viết cứng trong `Constants`.

## Tiện ích

- **Cách dùng:** gọi qua singleton `Utils.STR`, `Utils.O`, `Utils.CL`, `Utils.N`, `Utils.DATES`… Method là instance method, không phải static.
- **`Utils.O`:** bọc một Jackson `ObjectMapper` dùng chung (bỏ qua property lạ, ISO date, chuỗi rỗng thành `null`). `convertToType(...)` chuyển đổi theo `Class`, `JavaType`, `TypeReference` hoặc chuỗi kiểu.
- **`DateUtils`:** mặc định UTC. `NumberUtils.format` dùng dấu `.` ngăn cách hàng nghìn và `,` cho phần thập phân.
- **Deprecated:** `RefDataResolver` và `@RefProfileConfig`. Dùng `References` / `RecordMapper` thay thế.

## Build & phát hành

```bash
./gradlew compileJava                       # build nhanh
./gradlew clean publishToMavenLocal -x test # giống lệnh JitPack chạy
```

1. Merge thay đổi vào `main`.
2. Tạo GitHub Release với tag mới trên `main` (semver, không có tiền tố `v`, ví dụ `2.3.8`).
3. Mở <https://jitpack.io/#msm-lib/commons> và bấm **Get it** ở tag mới để build.
4. Cập nhật version ở các service dùng thư viện (ví dụ `msmCommonVersion` trong generic-object-management).

`jitpack.yml` cố định JDK `21.0.2-open` qua SDKMAN, vì alias `openjdk21` của JitPack có thể trỏ tới bản không tải được.

## Hạn chế đã biết

- `DefaultFilterQuery.filterStream(..., Consumer)` và `findByCondition(..., limit, ...)` **không áp điều kiện security**.
- `ErrorCodeEnum.FIELD_NOT_FOUND` đang mang mã `"OBJECT_CAST_ERROR"`.
- `PageRequest.addSort` ném NPE khi `sorts` đang `null`.
- `attrRef(table, ref, Class)` luôn dùng converter `Map`, nên giá trị scalar trong `custom_values` có thể đọc lỗi.
- Chưa có test tự động.
