package com.fantasticfive.shareback.newshareback.helpers;

import com.fantasticfive.shareback.newshareback.Constants;
import com.fantasticfive.shareback.newshareback.dto.RegisterRequestDTO;
import com.fantasticfive.shareback.newshareback.dto.RegisterResponseDTO;
import com.fantasticfive.shareback.newshareback.physical.RequestResponsePhysical;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sagar on 27/11/16.
 */
public class RegisterHelper implements RequestResponsePhysical.Callback{

    Callback callback;

    public RegisterHelper(Callback callback){
        this.callback = callback;
    }

    public void register(RegisterRequestDTO dto){
        RequestResponsePhysical physical = new RequestResponsePhysical(Constants.IP_FILE_SERVER, Constants.PORT_FEEDBACK, this);
        String requestDTO = new Gson().toJson(dto);
        try {
            JSONObject jsonDTO = new JSONObject(requestDTO);
            JSONObject main = new JSONObject();
            main.put(Constants.JSON_FB_TYPE, Constants.FB_EVENT_REGISTER);
            main.put(Constants.JSON_FB_VALUE, jsonDTO);

            physical.execute(main.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(String result) {
        RegisterResponseDTO dto = new Gson().fromJson(result, RegisterResponseDTO.class);
        callback.onRegisterResponse(dto);
    }

    public interface Callback{
        void onRegisterResponse(RegisterResponseDTO dto);
    }
}
