package com.fantasticfive.shareback.concept2.util;

import com.fantasticfive.shareback.concept2.bean.Rating;

/**
 * Created by sagar on 27/2/17.
 */
public class MathUtils {
    public static float avg(Rating rating){

        int r[] = {
                rating.getRating1(),
                rating.getRating2(),
                rating.getRating3(),
                rating.getRating4(),
                rating.getRating5(),
        };

        int sum = 0;
        int count = 0;
        for(int i=0; i<r.length; i++){
            sum += r[i]*(i+1);
            count += r[i];
        }

        if(count == 0)
            return 0f;

        try {
            float avg = (float) sum / count;
            return avg;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return 0f;
    }

    public static float percentage(int count, Rating rating){
        int total = rating.getRating1()+
                rating.getRating2()+
                rating.getRating3()+
                rating.getRating4()+
                rating.getRating5();
        if(total==0) return 0f;
        float pc = (count*100)/total;
        return pc;
    }
}
