import idnumber.EstonianIdNumber;
import idnumber.IdNumber;
import org.testng.Assert;
import org.testng.annotations.Test;
import web.Server;

import static org.testng.Assert.assertEquals;

class ServerTest {

    @Test
    void generateIdTest(String parameters) {
        IdNumber idNumber = new EstonianIdNumber("2020-13-12", "male");
        Assert.assertEquals(idNumber, Server.generateId("idgenerator?gender=male&birthday=2022-06-15"));

    }




}