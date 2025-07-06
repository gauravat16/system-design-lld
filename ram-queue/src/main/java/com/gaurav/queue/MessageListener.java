package com.gaurav.queue;

import com.gaurav.queue.model.Message;

public interface MessageListener {

     void onReceive(Message message);
}
