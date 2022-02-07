// DEFAULT FORMAT

'use strict';

import paymentForm from './component.payment-form';

const paymentPage = {
    init: function () {
        paymentForm.init();
    }
};

window.format = paymentPage;
window.format.init();

export default paymentPage;
