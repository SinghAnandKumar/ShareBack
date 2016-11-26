package com.fantasticfive.shareback.newshareback.helpers;

import com.fantasticfive.shareback.newshareback.Constants;
import com.fantasticfive.shareback.newshareback.dto.LoginRequestDTO;
import com.fantasticfive.shareback.newshareback.dto.LoginResponseDTO;
import com.fantasticfive.shareback.newshareback.physical.RequestResponsePhysical;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sagar on 19/11/16.
 */
public class LoginHelper implements RequestResponsePhysical.Callback{

    Callback callback;
    public LoginHelper(Callback callback){
        this.callback = callback;
    }

    public void sendLoginRequest(String username, String password){
        LoginRequestDTO dto = new LoginRequestDTO(username, password);
        RequestResponsePhysical physical = new RequestResponsePhysical(Constants.IP_FILE_SERVER, Constants.PORT_FEEDBACK, this);
        String dtoStr = new Gson().toJson(dto);
        try {
            JSONObject dtoJSON = new JSONObject(dtoStr);
            JSONObject main = new JSONObject();
            main.put(Constants.JSON_FB_TYPE, Constants.FB_EVENT_LOGIN);
            main.put(Constants.JSON_FB_VALUE, dtoJSON);

            physical.execute(main.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(String result) {
        LoginResponseDTO dto = new Gson().fromJson(result, LoginResponseDTO.class);
        callback.onLoginResponse(dto.getLoginToken());
    }

    public interface Callback{
        void onLoginResponse(String token);
    }
}