package com.noahhusby.sledgehammer.data.dialogs.components.location;

import com.noahhusby.sledgehammer.data.dialogs.components.DialogComponent;
import com.noahhusby.sledgehammer.util.TextElement;

public class CityComponent extends DialogComponent {
    @Override
    public String getKey() {
        return "city";
    }

    @Override
    public String getPrompt() {
        return "What city?";
    }

    @Override
    public TextElement[] getExplanation() {
        return null;
    }

    @Override
    public String[] getAcceptableResponses() {
        return new String[]{"*"};
    }

    @Override
    public boolean validateResponse(String v) {
        return true;
    }
}
