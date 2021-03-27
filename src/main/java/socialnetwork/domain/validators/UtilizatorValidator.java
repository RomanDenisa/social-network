package socialnetwork.domain.validators;

import socialnetwork.domain.Utilizator;

public class UtilizatorValidator implements Validator<Utilizator> {
    @Override
    public void validate(Utilizator entity) throws ValidationException {

        String errorMsg="";
        if(entity.getId() == null || entity.getId()<0)
            errorMsg+="Invalid ID";
        if(entity.getFirstName() == null || entity.getFirstName().equals(""))
            errorMsg+="Invalid First Name";
        if(entity.getLastName() == null || entity.getLastName().equals(""))
            errorMsg+="Invalid Last Name";
        if(errorMsg!="")
            throw new ValidationException(errorMsg);

    }
}
