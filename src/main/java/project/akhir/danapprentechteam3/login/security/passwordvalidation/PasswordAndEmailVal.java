package project.akhir.danapprentechteam3.login.security.passwordvalidation;

public interface PasswordAndEmailVal
{
    boolean PasswordValidatorLowercase(String password);
    boolean PasswordValidatorUpercase(String password);
    boolean PasswordValidatorNumber(String password);
    boolean PasswordValidatorSymbol(String password);
    boolean confirmPassword(String password, String confirmPassword);
    boolean EmailValidator (String password);
    boolean NumberOnlyValidator (String number);
    boolean LengthandNumeric (Long namaUser);
    boolean Alphabetic (String namaUser);
    boolean LengthPhoneNumber (String number);
    boolean LengthPassword (String number);
    boolean LengthUsername (String username);
    boolean LengthPin (Long pinTransaksi);
    boolean LengthPinNumeric (Long pinTransaksi);
    boolean PasswordValidatorSpace (String password);
}
