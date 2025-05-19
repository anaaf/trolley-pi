package com.crunch.cart.service;

import com.crunch.cart.entity.Cart;
import com.crunch.cart.repository.CartRepository;
import com.crunch.common.enums.Status;
import com.crunch.common.enums.Weight;
import com.crunch.user.model.WeightModel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WeightService {

    private final CartService cartService;
    private final CartRepository cartRepository;

    UUID trolleyUuid = UUID.fromString("99398387-c953-4d39-be4f-9950435df469");
    BigDecimal uncertainty = BigDecimal.valueOf(10);
    Weight uncertaintyUnit = Weight.GRAM;

    public void weightChangeEvent(WeightModel weightModel) {

        weightModel.setNewWeight(weightModel.getNewWeight().subtract(uncertainty));

        if(weightModel.getNewWeight().compareTo(weightModel.getOldWeight()) > 0) {
            // check in the cart that we have enough products
           Cart activeCart = cartRepository.
                    findFirstByTrolleyUuidAndStatusAndEnabledOrderByCreationDateDesc(
                            weightModel.getTrolleyUuid(), Status.ACTIVE, true
                    );

           BigDecimal cartWeight = activeCart.getWeight().setScale(2, BigDecimal.ROUND_HALF_UP);
//           BigDecimal trolleyNewWeight = weightModel.getNewWeight().setScale(2, BigDecimal.ROUND_HALF_UP);

        }
    }

}
