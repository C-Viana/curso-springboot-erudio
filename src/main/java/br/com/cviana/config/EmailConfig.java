package br.com.cviana.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.mail")
public class EmailConfig {
    private String HOST;
    private int PORT;
    private String username;
    private String password;
    private String FROM;
    private boolean SSL;

    public EmailConfig(){}

    public String getHOST() {
        return HOST;
    }

    public void setHOST(String hOST) {
        HOST = hOST;
    }

    public int getPORT() {
        return PORT;
    }

    public void setPORT(int pORT) {
        PORT = pORT;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String uSERNAME) {
        username = uSERNAME;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String pASSWORD) {
        password = pASSWORD;
    }

    public String getFROM() {
        return FROM;
    }

    public void setFROM(String fROM) {
        FROM = fROM;
    }

    public boolean isSSL() {
        return SSL;
    }

    public void setSSL(boolean sSL) {
        SSL = sSL;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((HOST == null) ? 0 : HOST.hashCode());
        result = prime * result + PORT;
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((FROM == null) ? 0 : FROM.hashCode());
        result = prime * result + (SSL ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EmailConfig other = (EmailConfig) obj;
        if (HOST == null) {
            if (other.HOST != null)
                return false;
        } else if (!HOST.equals(other.HOST))
            return false;
        if (PORT != other.PORT)
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        if (FROM == null) {
            if (other.FROM != null)
                return false;
        } else if (!FROM.equals(other.FROM))
            return false;
        if (SSL != other.SSL)
            return false;
        return true;
    }
}
