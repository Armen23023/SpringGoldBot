package am.relex.service.impl;

import am.relex.Controller.UpdateController;
import am.relex.service.AnswerConsumer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static am.relex.model.RabbitQueue.ANSWER_MESSAGE;

@Service
public class AnswerConsumerImpl implements AnswerConsumer {
    private final UpdateController updateController;

    public AnswerConsumerImpl(UpdateController updateController) {
        this.updateController = updateController;

    }


    @Override
    @RabbitListener(queues = ANSWER_MESSAGE)
    public void consume(SendMessage sendMessage) {
        updateController.setView(sendMessage);
    }
}
