package jp.ddo.masm11.lounlocker;

import java.io.File;

public class Config {
    public final String hostname;
    public final String username;
    public final File privkey;
    public final String pwd_sudo;
    public final String pwd_decr;
    public Config(String hostname, String username, File privkey,
	    String pwd_sudo, String pwd_decr) {
	this.hostname = hostname;
	this.username = username;
	this.privkey = privkey;
	this.pwd_sudo = pwd_sudo;
	this.pwd_decr = pwd_decr;
    }
}
