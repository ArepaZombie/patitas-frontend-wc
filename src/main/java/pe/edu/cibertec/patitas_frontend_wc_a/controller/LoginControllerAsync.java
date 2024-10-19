package pe.edu.cibertec.patitas_frontend_wc_a.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.cibertec.patitas_frontend_wc_a.clients.AutenticacionCliente;
import pe.edu.cibertec.patitas_frontend_wc_a.dto.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/login")
@CrossOrigin(origins = "http://localhost:5173")
public class LoginControllerAsync {

  @Autowired
  WebClient webClientAutenticacion;
  //RestTemplate restTemplate;

  @Autowired
  AutenticacionCliente autenticacionClient;

  @PostMapping("/autenticar-async")
  public Mono<ResponseLogin> autenticar(@RequestBody RequestLogin requestLogin) {

    //Validamos los campos
    if (requestLogin.tipoDocumento() == null || requestLogin.tipoDocumento().trim().length() == 0 ||
    requestLogin.numeroDocumento() == null || requestLogin.numeroDocumento().trim().length() == 0 ||
      requestLogin.password() == null || requestLogin.password().trim().length() == 0){
      //En caso no ingresen todos los datos, salta este error
      return Mono.just(new ResponseLogin("01","Datos insuficientes","",""));
    }

    try {
      //hacemos la solicitud

      //y recibimos la data (response)
      return webClientAutenticacion.post()
        .uri("/login")
        .body(Mono.just(requestLogin), RequestLogin.class)
        .retrieve()
        .bodyToMono(ResponseLogin.class)
        .flatMap(response -> {
          //manipulación... cambia el "return" que le daremos
          if(response.codigo().equals("00")){
            return Mono.just(new ResponseLogin(
              "00","",response.nombreUsuario(), response.correoUsuario()));
          }else {
            return Mono.just(new ResponseLogin(
              "02","Autenticación fallida","",""));
          }

        });
      //si le agregamos un .block lo hacemos sincronico
      //osea que bloquea el proceso hasta que recibimos una sorpresa
      //ResponseLogin response = monoResponse.block();

    }catch (Exception e){
      System.out.println(e.getMessage());
      return Mono.just(new ResponseLogin("99",e.getMessage(),"",""));
    }

  }

  @PostMapping("/close-async")
  public Mono<ResponseClose> cerrarSesion(@RequestBody RequestClose request){
      try{
        return webClientAutenticacion.post()
          .uri("/close")
          .body(Mono.just(request),RequestClose.class)
          .retrieve()
          .bodyToMono(ResponseClose.class)
          .flatMap(response -> {
            if(response.codigo().equals("00")){
              return Mono.just(new ResponseClose("00","Sesión cerrada"));
            }else {
              return Mono.just(new ResponseClose("01","Hubo un problema en el servicio"));
            }
          });
      } catch (Exception e) {
        System.out.println(e.getMessage());
        return Mono.just(new ResponseClose("99",e.getMessage()));
      }
  }

  //Como es síncrono no se devuelve Mono, solo el Response
  @PostMapping("/close-ef")
  public ResponseCloseEF cerrarSesion(@RequestBody RequestCloseEF request){
    try {
      //consumimos servicio con Feign Client
      ResponseEntity<ResponseCloseEF> response = autenticacionClient.closeEF(request);
      System.out.println("Cerrando sesión con Feign :D");
      if(response.getStatusCode().is2xxSuccessful()){
        //recuperamos y retornamos response
        return response.getBody();
      } else {
        return new ResponseCloseEF("99","Ocurrió un problema con el servicio");
      }

    }catch (Exception e){
      return new ResponseCloseEF("99",e.getMessage());
    }

  }

}
