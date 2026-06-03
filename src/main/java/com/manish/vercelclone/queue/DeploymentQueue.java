package com.manish.vercelclone.queue;

import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;

@Component
public class DeploymentQueue {

    LinkedBlockingQueue<Long> lbq = new LinkedBlockingQueue<>();

    public void addDeployment(Long id){

        lbq.add(id);
    }

    public Long takeDeployment() throws InterruptedException {

       return lbq.take() ;

    }


}
