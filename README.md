# 股票分析系统
## 服务端 hn-market-sharding
### 分库分表 shardingsphere
1. pox.xml 引入
    
```
        <dependency>
            <groupId>org.apache.shardingsphere</groupId>
            <artifactId>sharding-jdbc-spring-boot-starter</artifactId>
            <version>4.0.0-RC1</version>
        </dependency>

        <dependency>
            <groupId>org.apache.shardingsphere</groupId>
            <artifactId>sharding-jdbc-spring-namespace</artifactId>
            <version>4.0.0-RC1</version>
        </dependency>
```

2. application.yml 配置  
    数据库 ：hnmarket0,hnmarket1  
    表：mk_indiv_day  
    数据库sharding-column:smarket  
    表sharding-column:scode  
    
```
spring:
  main:
    allow-bean-definition-overriding: true # shardingsphere  一个实体类对应两张表，覆盖
  shardingsphere:
    datasource:
      names: hnmarket0,hnmarket1
      hnmarket0:
        type: com.alibaba.druid.pool.DruidDataSource
        driver-class-name: com.mysql.jdbc.Driver
        url: jdbc:mysql://localhost:3306/hnmarket0?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false&rewriteBatchedStatements=true
        username: root
        password: 123456
      hnmarket1:
        type: com.alibaba.druid.pool.DruidDataSource
        driver-class-name: com.mysql.jdbc.Driver
        url: jdbc:mysql://localhost:3306/hnmarket1?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false&rewriteBatchedStatements=true
        username: root
        password: 123456
    sharding:
      default-database-strategy:
        inline:
          sharding-column: smarket
          algorithm-expression: hnmarket$->{smarket % 2}
      tables:
        mk_indiv_day:
          actual-data-nodes: hnmarket$->{0..1}.mk_indiv_day$->{0..19}
          table-strategy:
#            inline:
#              sharding-column: scode_num
#              algorithm-expression: mk_indiv_day$->{scode_num % 20}
            standard: #自定义分片规则类
              sharding-column: scode
              precise-algorithm-class-name: com.hn.market.common.utils.ShardingTableKeyUtils
#          key-generator:
#            #主键id 生成策略  SNOWFLAKE
#            column: tenant_id
#            type: SNOWFLAKE
    props:
      sql:
        show: true
```

3. 自定义分片规则类
    
```
@Component
public class ShardingTableKeyUtils implements PreciseShardingAlgorithm<String> {
    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<String> preciseShardingValue) {
        String id = preciseShardingValue.getValue();
        int tableNo = 0;
        int num=0;
        if (StrUtil.isNotEmpty(id)){
            tableNo = Integer.valueOf(id) % 20;
        }else{
            return null;
        }
        for (String tableName : collection) {//循环表名已确定使用哪张表
            int len=tableName.length();
            if(48<=tableName.charAt(len-2) && tableName.charAt(len-2) <=57){
               num = Integer.valueOf(tableName.substring(len-2));
            }else{
                num = Integer.valueOf(tableName.substring(len-1));
            }

            if(num==tableNo){
                return tableName;//返回要插入的逻辑表
            }
        }
        return null;
    }
}
```
### Swagger API接口
1.     引入依赖包
```
        <!--Swagger-UI API文档生产工具-->
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-boot-starter</artifactId>
            <version>${springfox-swagger.version}</version>
        </dependency>
```

2.     添加配置

```
@Configuration
@EnableSwagger2
public class Swagger2Config extends BaseSwaggerConfig {

    @Override
    public SwaggerProperties swaggerProperties() {
        return SwaggerProperties.builder()
                .apiBasePackage("com.hn.market.indiv.controller")
                .title("hn-stock项目骨架")
                .description("hn-stock项目骨架相关接口文档")
                .contactName("Admin")
                .version("1.0")
                .enableSecurity(true)
                .build();
    }

    @Bean
    public static BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
        return new BeanPostProcessor() {

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof WebMvcRequestHandlerProvider || bean instanceof WebFluxRequestHandlerProvider) {
                    customizeSpringfoxHandlerMappings(getHandlerMappings(bean));
                }
                return bean;
            }

            private <T extends RequestMappingInfoHandlerMapping> void customizeSpringfoxHandlerMappings(List<T> mappings) {
                List<T> copy = mappings.stream()
                        .filter(mapping -> mapping.getPatternParser() == null)
                        .collect(Collectors.toList());
                mappings.clear();
                mappings.addAll(copy);
            }

            @SuppressWarnings("unchecked")
            private List<RequestMappingInfoHandlerMapping> getHandlerMappings(Object bean) {
                try {
                    Field field = ReflectionUtils.findField(bean.getClass(), "handlerMappings");
                    field.setAccessible(true);
                    return (List<RequestMappingInfoHandlerMapping>) field.get(bean);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            }
        };
    }

}
```

```
public abstract class BaseSwaggerConfig {

    @Bean
    public Docket createRestApi() {
        SwaggerProperties swaggerProperties = swaggerProperties();
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo(swaggerProperties))
                .select()
                .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getApiBasePackage()))
                .paths(PathSelectors.any())
                .build();
        if (swaggerProperties.isEnableSecurity()) {
            docket.securitySchemes(securitySchemes()).securityContexts(securityContexts());
        }
        return docket;
    }

    private ApiInfo apiInfo(SwaggerProperties swaggerProperties) {
        return new ApiInfoBuilder()
                .title(swaggerProperties.getTitle())
                .description(swaggerProperties.getDescription())
                .contact(new Contact(swaggerProperties.getContactName(), swaggerProperties.getContactUrl(), swaggerProperties.getContactEmail()))
                .version(swaggerProperties.getVersion())
                .build();
    }

    private List<SecurityScheme> securitySchemes() {
        //设置请求头信息
        List<SecurityScheme> result = new ArrayList<>();
        ApiKey apiKey = new ApiKey("Authorization", "Authorization", "header");
        result.add(apiKey);
        return result;
    }

    private List<SecurityContext> securityContexts() {
        //设置需要登录认证的路径
        List<SecurityContext> result = new ArrayList<>();
        result.add(getContextByPath("/*/.*"));
        return result;
    }

    private SecurityContext getContextByPath(String pathRegex) {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex(pathRegex))
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        List<SecurityReference> result = new ArrayList<>();
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        result.add(new SecurityReference("Authorization", authorizationScopes));
        return result;
    }

    /**
     * 自定义Swagger配置
     */
    public abstract SwaggerProperties swaggerProperties();
}
```

```
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
public class SwaggerProperties {
    /**
     * API文档生成基础路径
     */
    private String apiBasePackage;
    /**
     * 是否要启用登录认证
     */
    private boolean enableSecurity;
    /**
     * 文档标题
     */
    private String title;
    /**
     * 文档描述
     */
    private String description;
    /**
     * 文档版本
     */
    private String version;
    /**
     * 文档联系人姓名
     */
    private String contactName;
    /**
     * 文档联系人网址
     */
    private String contactUrl;
    /**
     * 文档联系人邮箱
     */
    private String contactEmail;
}
```

3.     controller 类配置

```
@CrossOrigin
@RestController
@RequestMapping("/mkIndivDay")
@Api(tags = "MkIndivDayController", description = "个股行情")
public class MkIndivDayController {

    @Autowired
    private MkIndivDayService mkIndivDayService;

    @ApiOperation("查询个股行情（包含沪深京A股）")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<MkIndivDay>> list(@RequestParam(value = "smarket", required = false) String smarket,
                                                       @RequestParam(value = "scode", required = false) String scode,
                                                       @RequestParam(value = "sname", required = false) String sname,
                                                       @RequestParam(value = "sdate", required = false) String sdate,
                                                        @RequestParam(value = "edate", required = false) String edate,
                                                       @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                       @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<MkIndivDay> list = mkIndivDayService.list(smarket,scode,sname,sdate,edate,pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(list,pageSize,pageNum));
    }
}
```

4.  测试地址
     http://localhost:8086/swagger-ui/index.html


