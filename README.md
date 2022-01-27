# vality-http-client
Обертка над apache-http-client для упрощения конфигурации

Если необходимо инициализируем пул клиентов (в данном примере это пул для ssl клиентов)
```
@Bean
    public HttpClientPool<SslRequestConfig> clientPool(KeyStoreProperties keyStoreProperties) {
        return new SslCertHttpClientPool(
                new HttpClientFactory(timeout, maxPerRoute, maxTotal, keyStoreProperties), SslRequestConfig::getCertFileName);
    }
```

Создаем клиент, который будет отвечать за вызов, с включением метрик
```
    @Bean
    public HttpClient httpClient(MeterRegistry meterRegistry) {
        return SimpleHttpClient.builder()
                .registry(meterRegistry)
                .enableMetrics(true)
                .build();
    }
```

За создание запросов отвечает RequestFactory, 
позволяет создавать запросы с установкой таймаутов на каждый конкретный запрос
```
    @Bean
    public UrlParamsRequestFactory urlParamsRequestFactory(MeterRegistry meterRegistry) {
        return new UrlParamsRequestFactory();
    }
```