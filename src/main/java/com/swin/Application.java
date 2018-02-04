package com.swin;

import com.swin.common.SwinServer;
import com.swin.db.MapDBFactory;
import com.swin.exception.ServerStartException;
import com.swin.manager.ConditionLock;
import com.swin.server.ParamsLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by LiaoYuecai on 2017/9/29.
 */
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception {
        MapDBFactory.init();
        SwinServer.init(ParamsLoader.getPort());
//        DBBackups.init();
        boolean flag = (boolean) ConditionLock.getInstance().await("server_start", 30000);
        if (flag) {
            logger.info("Server has been started");
        } else {
            throw new ServerStartException("Server start failed");
        }
    }


}
