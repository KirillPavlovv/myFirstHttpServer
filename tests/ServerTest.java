import idnumber.EstonianIdNumber;
import idnumber.IdNumber;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;
import static web.Server.generateId;

class ServerTest {

    @Test
    void generateIdTest(String parameters) {
        IdNumber idNumber = new EstonianIdNumber("2020-13-12", "male");
        assertEquals(idNumber, generateId("idgenerator?gender=male&birthday=2022-06-15"));

    }




}