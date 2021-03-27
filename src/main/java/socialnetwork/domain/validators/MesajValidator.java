package socialnetwork.domain.validators;

import socialnetwork.domain.Message;

public class MesajValidator implements Validator<Message> {
    @Override
    public void validate(Message entity) throws ValidationException {
        String errors="";
        if(entity.getMessage() == null || entity.getMessage().equals(""))
            errors+="Invalid Message";
        if(errors!="")
            throw new ValidationException(errors);
    }
}
