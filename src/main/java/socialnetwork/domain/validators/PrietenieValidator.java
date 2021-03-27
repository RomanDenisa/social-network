package socialnetwork.domain.validators;

import socialnetwork.domain.Prietenie;

public class PrietenieValidator implements Validator<Prietenie> {

    @Override
    public void validate(Prietenie entity) throws ValidationException {
        String errorMsg="";
        if(entity.getId() == null)
            errorMsg+="id can not be null";
        if(entity.getId().getLeft() == null || entity.getId().getLeft()<0)
            errorMsg+="invalid first user id";
        if(entity.getId().getRight() == null || entity.getId().getRight()<0)
            errorMsg+="invalid second user id";
        if(entity.getDate() == null)
            errorMsg+="invalid data";
        if(entity.getId().getLeft().equals(entity.getId().getRight()))
            errorMsg+="first user id can not be equal to the second user id";
        if(errorMsg!="")
            throw new ValidationException(errorMsg);
    }
}
