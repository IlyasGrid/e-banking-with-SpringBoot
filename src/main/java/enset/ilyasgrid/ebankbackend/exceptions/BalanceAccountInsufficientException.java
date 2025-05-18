package enset.ilyasgrid.ebankbackend.exceptions;

public class BalanceAccountInsufficientException extends Exception {
    public BalanceAccountInsufficientException(String message) {
        super(message);
    }
}