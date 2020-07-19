package com.zzz.service.module.controller;

import com.zzz.basemodels.ResultModel;
import com.zzz.basemodels.enummodel.ResponseCode;
import com.zzz.entitymodel.servicebase.DO.ActorModel;
import com.zzz.service.module.params.TestParams;
import com.zzz.service.module.service.ActorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author ZZZ
 *  Test for controller
 */
@Controller
@RequestMapping("/service/actor")
public class ActorController {
    private static Logger LOG = LoggerFactory.getLogger(ActorController.class);
    @Autowired
    private ActorService actorService;

    @RequestMapping(value = "queryActor",method = RequestMethod.POST,produces={MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResultModel<List<ActorModel>> queryActors(HttpServletRequest req, @RequestBody TestParams testParams){
        LOG.debug("Params:{}",testParams.toString());
//        TestParams testParams = new TestParams();
//        testParams.setLastName(lastName);
        List<ActorModel> resultModel = actorService.actorModels(testParams);
        return ResultModel.buildResult(resultModel, ResponseCode.SUCCESS);
    }
}
