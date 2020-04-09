package cz.craftmania.craftlibs.exceptions;

public class SQLNotEnabledException extends Exception {

    public SQLNotEnabledException(){
        super("SQL is not enabled in CraftLibs config file.");
    }
}
