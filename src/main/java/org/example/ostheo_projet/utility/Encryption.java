package org.example.ostheo_projet.utility;

import com.password4j.BcryptFunction;
import com.password4j.Hash;
import com.password4j.Password;
import com.password4j.types.Bcrypt;

public class Encryption {

    private final static String pepperString = "pepper";

    public static String hashString(String str){
        BcryptFunction bcrypt = BcryptFunction.getInstance(Bcrypt.B,12);
        Hash hash = Password.hash(str).addPepper(pepperString).with(bcrypt);
        return hash.getResult();
    }

    public static boolean verifyPassword(String password, String hashedPassword){
        BcryptFunction bcrypt = BcryptFunction.getInstance(Bcrypt.B,12);
        return Password.check(password, hashedPassword).addPepper(pepperString).with(bcrypt);
    }
}
