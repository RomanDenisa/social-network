package socialnetwork.domain.validators;

import socialnetwork.domain.Cont;

public class ContValidator implements Validator<Cont> {
    @Override
    public void validate(Cont entity) throws ValidationException {
        String errorMsg="";
        if(entity.getId() == null || entity.getId()<0)
            errorMsg+="Invalid ID";
        if(entity.getUsername() == null || entity.getUsername().equals(""))
            errorMsg+="Invalid Username";
        if(entity.getPassword() == null || entity.getPassword().equals(""))
            errorMsg+="Invalid Password";
        if(errorMsg!="")
            throw new ValidationException(errorMsg);
    }
}
