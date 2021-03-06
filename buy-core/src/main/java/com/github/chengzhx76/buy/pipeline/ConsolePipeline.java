package com.github.chengzhx76.buy.pipeline;

import com.github.chengzhx76.buy.model.Request;
import com.github.chengzhx76.buy.model.Response;

/**
 * @desc:
 * @author: hp
 * @date: 2018/1/12
 */
public class ConsolePipeline implements Pipeline {
    @Override
    public void process(Request request, Response response) {
        if (response.isDestroy()) {
            return;
        }
        System.out.println(response.getOperation()+"\r\n" +
                "msg--> "+response.getRawText()+"\r\n" +
                "url--> "+request.getUrl()+"\r\n");
    }
}
