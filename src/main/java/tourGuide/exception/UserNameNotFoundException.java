package tourGuide.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserNameNotFoundException extends RuntimeException {

    public UserNameNotFoundException(String userName){
        super("This userName is not found : " + userName);
        log.error("This userName is not found : " + userName);
    }


}
