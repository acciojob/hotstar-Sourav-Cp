package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());

        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());

        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();

        int amount = 0;
        if(subscription.getSubscriptionType().equals(SubscriptionType.BASIC))
        {
            amount = 500 + (200*subscriptionEntryDto.getNoOfScreensRequired());
        } else if (subscription.getSubscriptionType().equals(SubscriptionType.PRO)) {
            amount = 800 + (250*subscriptionEntryDto.getNoOfScreensRequired());
        }
        else amount = 1000 + (305*subscriptionEntryDto.getNoOfScreensRequired());

        subscription.setTotalAmountPaid(amount);
        subscription.setUser(user);

        user.setSubscription(subscription);

        userRepository.save(user);
        return amount;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user = userRepository.findById(userId).get();

        if(user.getSubscription().equals(SubscriptionType.ELITE)) throw new Exception("Already the best Subscription");

        int newAmount = 0;
        int prevAmount = user.getSubscription().getTotalAmountPaid();

        if(user.getSubscription().getSubscriptionType().equals(SubscriptionType.BASIC))
        {
            newAmount = 800 + (250*user.getSubscription().getNoOfScreensSubscribed());
            user.getSubscription().setSubscriptionType(SubscriptionType.PRO);
        }
        else{
            newAmount = 100 + (250*user.getSubscription().getNoOfScreensSubscribed());
            user.getSubscription().setSubscriptionType(SubscriptionType.ELITE);
        }


        user.getSubscription().setTotalAmountPaid(newAmount);
        userRepository.save(user);

        return newAmount - prevAmount;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        List<Subscription> subscriptionList = subscriptionRepository.findAll();
        int amount = 0;

        for (Subscription subscription : subscriptionList)
        {
            amount += subscription.getTotalAmountPaid();
        }

        return amount;
    }

}
