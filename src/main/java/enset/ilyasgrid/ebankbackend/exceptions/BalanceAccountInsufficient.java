package enset.ilyasgrid.ebankbackend.exceptions;

public class BalanceAccountInsufficient extends Exception {
    public BalanceAccountInsufficient(String message) {
        super(message);
    }
}