package socialnetwork.domain.validators;

import socialnetwork.domain.CererePrietenie;
import socialnetwork.domain.StatusEnum;

public class CerereValidator implements Validator<CererePrietenie> {

    @Override
    public void validate(CererePrietenie entity) throws ValidationException {
        String errorMsg="";
        if(entity.getId() == null)
            errorMsg+="id can not be null";
        if(entity.getId().getLeft().equals(entity.getId().getRight()))
            errorMsg+="first user id can not be equal to the second user id";
        if(errorMsg!="")
            throw new ValidationException(errorMsg);
    }
}
