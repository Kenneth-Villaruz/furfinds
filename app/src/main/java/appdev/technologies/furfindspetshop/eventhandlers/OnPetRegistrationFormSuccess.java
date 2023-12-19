package appdev.technologies.furfindspetshop.eventhandlers;

import appdev.technologies.furfindspetshop.recyclerviews.MyPetsItem;

public interface OnPetRegistrationFormSuccess
{
    void registerSuccess(int rowId, MyPetsItem item);
}
