package com.fantasticfive.shareback.newshareback.fileoperation;

import com.fantasticfive.shareback.newshareback.Constants;
import com.fantasticfive.shareback.newshareback.dto.SessionDTO;
import com.fantasticfive.shareback.newshareback.physical.RequestResponsePhysical;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sagar on 5/11/16.
 */
public class SessionDetailsFetcher implements RequestResponsePhysical.Callback{

    Callback callback;
    boolean cancelled = false;

    public SessionDetailsFetcher(Callback callback){
        this.callback = callback;
    }

    public void fetch(String sessionId){
        RequestResponsePhysical request = new RequestResponsePhysical(Constants.IP_FILE_SERVER, Constants.PORT_FEEDBACK, this);
        JSONObject main = new JSONObject();
        try {
            main.put(Constants.JSON_FB_TYPE, Constants.FB_EVENT_SESSION_DETAILS);
            main.put(Constants.JSON_FB_SESSION_ID, sessionId);
        }
        catch(JSONException e){
            e.printStackTrace();
        }
        request.execute(main.toString());
    }

    public void cancel(){
        cancelled=true;
    }

    @Override
    public void onResponse(String msg) {
        SessionDTO dto = new Gson().fromJson(msg, SessionDTO.class);
        if(!cancelled)
            callback.onSessionDetailsReceived(dto);
    }

    public interface Callback{
        void onSessionDetailsReceived(SessionDTO dto);
    }
}
