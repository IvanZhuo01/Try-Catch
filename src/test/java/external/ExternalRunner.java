package external;

import com.intuit.karate.junit5.Karate;

class ExternalRunner {
    
    
    @Karate.Test
    Karate testPrincipal() {
        return Karate.run("flujoprincipal").relativeTo(getClass());
    }  

 
    
    @Karate.Test
    Karate testProfesor() {
        return Karate.run("serProfesor").relativeTo(getClass());
    }  

    @Karate.Test
    Karate testWs() {
        return Karate.run("registro").relativeTo(getClass());
    }  
}
