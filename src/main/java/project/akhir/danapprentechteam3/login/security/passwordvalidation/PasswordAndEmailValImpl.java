package project.akhir.danapprentechteam3.login.security.passwordvalidation;

import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PasswordAndEmailValImpl implements PasswordAndEmailVal
{
    private Pattern pattern;
    private Matcher matcher;

    // "^[0-9]+$"
    private static final String PASSWORD_PATTERN_NUMBER     =
            ".*\\d.*";
    private static final String NUMBER_ONLY                 =
            "\\+?([ -]?\\d+)+|\\(\\d+\\)([ -]\\d+)";
    private static final String PASSWORD_PATTERN_LOWERCASE  =
            ".*[a-z].*";
    private static final String PASSWORD_PATTERN_UPPERCASE  =
            ".*[A-Z].*";
    private static final String PASSWORD_PATTERN_SYMBOL     =
            ".*[@#$%!].*";
    private static final String EMAIL_REGEX                 =
            "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
    private static final String ALPHABET                    =
            "[a-zA-Z\\s']+";
    private static final String ALPHABET_REGEX              =
            "^[0-9]{6}$";

    @Override
    public boolean PasswordValidatorLowercase(String password)
    {
        pattern = Pattern.compile(PASSWORD_PATTERN_LOWERCASE);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    @Override
    public boolean PasswordValidatorUpercase(String password)
    {
        pattern = Pattern.compile(PASSWORD_PATTERN_UPPERCASE);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    @Override
    public boolean PasswordValidatorNumber(String password){
        pattern = Pattern.compile(PASSWORD_PATTERN_NUMBER);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    @Override
    public boolean PasswordValidatorSymbol(String password){
        pattern = Pattern.compile(PASSWORD_PATTERN_SYMBOL);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    @Override
    public boolean confirmPassword(String password, String confirmPassword){
        boolean status = false;
        if (password.equals(confirmPassword))
        {
            status = true;
        }
        return status;
    }

    @Override
    public boolean EmailValidator (String password)
    {
        pattern = Pattern.compile(EMAIL_REGEX);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    @Override
    public boolean NumberOnlyValidator (String number)
    {
        pattern = Pattern.compile(NUMBER_ONLY);
        matcher = pattern.matcher(number);
        return matcher.matches();
    }

    @Override
    public boolean LengthandNumeric (Long namaUser){
        pattern = Pattern.compile(ALPHABET_REGEX);
        matcher = pattern.matcher(String.valueOf(namaUser));
        return matcher.matches();
    }

    @Override
    public boolean Alphabetic (String namaUser){
        pattern = Pattern.compile(ALPHABET);
        matcher = pattern.matcher(namaUser);
        return matcher.matches();
    }

    @Override
    public boolean LengthPhoneNumber (String number){
        boolean status;
        if (number.length() >= 16)
        {
            return status = false;
        }
            return status = true;
    }

    @Override
    public boolean LengthPassword (String number)
    {
        boolean status;

        if (number.length() > 7 && number.length() < 17)

        {
            return status = true;
        }
            return status = false;
    }

    @Override
    public boolean LengthUsername (String username)
    {
        boolean status;
        if (username.length() < 3){
            return status = false;
        }
        return status = true;
    }

    @Override
    public boolean LengthPin (Long pinTransaksi){
        boolean status;

        String pin = String.valueOf(pinTransaksi);
        pattern = Pattern.compile(NUMBER_ONLY);
        matcher = pattern.matcher(pin);
        if (pin.length() == 6){
            return status = true;
        } else {
            return status = false;
        }

    }

    @Override
    public boolean LengthPinNumeric (Long pinTransaksi)

    {
        boolean status;

        String pin = String.valueOf(pinTransaksi);
        pattern = Pattern.compile(NUMBER_ONLY);
        matcher = pattern.matcher(pin);

        if (matcher.matches())
        {
            status = true;
        } else {
            status = false;
        }

        return status = true;
    }

    @Override
    public boolean PasswordValidatorSpace (String password){
        if(password != null){
            for(int i = 0; i < password.length(); i++){
                if(Character.isWhitespace(password.charAt(i))){
                    return false;
                }
            }
        }
        return true;
    }
}
