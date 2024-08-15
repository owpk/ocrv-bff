package com.ocrf.bff.common.service;

//@Service
//@RequiredArgsConstructor
public class ClientService {

  //  private final NamedParameterJdbcTemplate jdbcTemplate;

 //   private final ClientRepository clientRepository;

   // @Transactional(isolation = Isolation.READ_COMMITTED)
//    public String getSecretByClientId(String clientId) {
//        Assert.hasText(clientId, "clientId cannot be empty");
//        SqlParameterSource parameters = new MapSqlParameterSource("clientId", clientId);
//        String sql = "select c.client_secret from  oauth2_registered_client c where c.client_id = :clientId";
//        List<String> secrets = jdbcTemplate.query(sql, parameters,    (resultSet, i) -> resultSet.getString("client_secret"));
//      //  Client client =  this.clientRepository.findByClientId(clientId).orElse(null);
//        if (! (secrets.isEmpty() || secrets.get(0)==null)) {
//            String pass = null;
//            if (secrets.get(0).contains("}")) {
//                pass = secrets.get(0).substring(secrets.get(0).indexOf("}")+1);
//            } else{
//                pass = secrets.get(0);
//            }
//            if (pass == null) {
//                throw new CztRuntimeException(Messages.INTERNAL_SERVER_ERROR, "не найден зарегестрированный клиент");
//            }
//            return pass;
//        }
//        throw new CztRuntimeException(Messages.INTERNAL_SERVER_ERROR, "не найден зарегестрированный клиент");
//    }



}
