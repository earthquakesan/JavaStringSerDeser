package org.ermilov.SerializationBenchmark.JavaSerialization;

import org.junit.Test;
import org.xerial.snappy.Snappy;

import java.io.*;
import java.util.Base64;

import static org.junit.Assert.*;

public class SerTest {
    @Test public void serializeToDisk() {
        byte[] encodedTed = null;
        try
        {
            Person ted = new Person("Ted", "Neward", 39);
            Person charl = new Person("Charlotte","Neward", 38);
            ted.setSpouse(charl); charl.setSpouse(ted);

            encodedTed = toString(ted);
        }
        catch (Exception ex)
        {
            fail("Exception thrown during test: " + ex.toString());
        }

        try
        {
            Person tedDeserialized = (Person) fromString(encodedTed);
            assertEquals(tedDeserialized.getFirstName(), "Ted");
            assertEquals(tedDeserialized.getSpouse().getFirstName(), "Charlotte");
        }
        catch (Exception ex)
        {
            fail("Exception thrown during test: " + ex.toString());
        }
    }

    private static byte[] toString(Serializable obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.close();
        String stringToSend = Base64.getEncoder().encodeToString(baos.toByteArray());
        byte[] bytesToSend = stringToSend.getBytes();
        return Snappy.compress(bytesToSend);
    }

    private static Object fromString(byte[] compressedString) throws IOException, ClassNotFoundException {
        byte[] uncompressedString = Snappy.uncompress(compressedString);
        String receivedString = new String(uncompressedString);
        byte[] data = Base64.getDecoder().decode(receivedString);
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(  data ) );
        Object o  = ois.readObject();
        ois.close();
        return o;
    }
}