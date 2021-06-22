<#if feedbackIsEnabled?? && feedbackIsEnabled = true>

<section id="feedback" class="gov_feedback">
    <div class="ds_error-summary  fully-hidden" id="feedbackErrorSummary" aria-labelledby="error-summary-title" role="alert">
        <h2 class="ds_error-summary__title" id="error-summary-title">There is a problem</h2>
        <div class="ds_error-summary__content">

        </div>
    </div>

    <div id="feedbackThanks" class="ds_reversed  fully-hidden">
        <p><strong>Thanks for your feedback</strong></p>
    </div>

    <form id="feedback-form">
        <input id="page-category" type="hidden" value="${layoutName}" />

        <fieldset id="feedbacktype">
            <legend>Was this helpful?</legend>

            <div class="ds_field-group">
                <div class="ds_radio  ds_radio--small">
                    <input type="radio" class="ds_radio__input" id="needsmetyes" name="feedbacktype" value="yes">
                    <label class="ds_radio__label" for="needsmetyes">Yes</label>

                    <div class="ds_reveal-content">
                        <div class="ds_question">
                            <label class="ds_label" for="comments-yes">Your comments</label><br>
                            <textarea rows="5" maxlength="250" class="ds_input" id="comments-yes"></textarea>

                            <p><strong>Note:</strong> Your feedback will help us make improvements on this site. Please do not provide any <a href="https://ico.org.uk/for-organisations/guide-to-data-protection/guide-to-the-general-data-protection-regulation-gdpr/key-definitions/what-is-personal-data/">personal information</a></p>
                        </div>

                        <button type="submit" class="ds_button  ds_no-margin">Send feedback</button>
                    </div>
                </div>

                <div class="ds_radio  ds_radio--small">
                    <input type="radio" class="ds_radio__input" id="needsmetno" name="feedbacktype" value="no">
                    <label class="ds_radio__label" for="needsmetno">No</label>

                    <div class="ds_reveal-content">
                        <div class="ds_question">
                            <label class="ds_label" for="reason-no">Choose a reason for your feedback</label>
                            <div class="ds_select-wrapper">
                                <select id="reason-no" class="ds_select">
                                    <option value="" selected disabled>Please select a reason</option>
                                    <option>It wasn't detailed enough</option>
                                    <option>It's hard to understand</option>
                                    <option>It's incorrect</option>
                                    <option>It needs updating</option>
                                    <option>There's a broken link</option>
                                    <option>It wasn't what I was looking for</option>
                                    <option>Other</option>
                                </select>
                                <span class="ds_select-arrow" aria-hidden="true"></span>
                            </div>
                        </div>

                        <div class="ds_question">
                            <label class="ds_label" for="comments-no">Your comments</label><br>
                            <textarea rows="5" maxlength="250" class="ds_input" id="comments-no"></textarea>

                            <p><strong>Note:</strong> Your feedback will help us make improvements on this site. Please do not provide any <a href="https://ico.org.uk/for-organisations/guide-to-data-protection/guide-to-the-general-data-protection-regulation-gdpr/key-definitions/what-is-personal-data/">personal information</a></p>
                        </div>

                        <button type="submit" class="ds_button  ds_no-margin">Send feedback</button>
                    </div>
                </div>

                <div class="ds_radio  ds_radio--small">
                    <input type="radio" class="ds_radio__input" id="needsmetyesbut" name="feedbacktype" value="yesbut">
                    <label class="ds_radio__label" for="needsmetyesbut">Yes, but</label>

                    <div class="ds_reveal-content">
                        <div class="ds_question">
                            <label class="ds_label" for="reason-no">Choose a reason for your feedback</label>
                            <div class="ds_select-wrapper">
                                <select id="reason-yesbut" class="ds_select">
                                    <option value="" selected disabled>Please select a reason</option>
                                    <option>It needs updating</option>
                                    <option>There's a spelling mistake</option>
                                    <option>It's hard to understand</option>
                                    <option>There's a broken link</option>
                                    <option>Other</option>
                                </select>
                                <span class="ds_select-arrow" aria-hidden="true"></span>
                            </div>
                        </div>

                        <div class="ds_question">
                            <label class="ds_label" for="comments-yesbut">Your comments</label><br>
                            <textarea rows="5" maxlength="250" class="ds_input" id="comments-yesbut"></textarea>

                            <p><strong>Note:</strong> Your feedback will help us make improvements on this site. Please do not provide any <a href="https://ico.org.uk/for-organisations/guide-to-data-protection/guide-to-the-general-data-protection-regulation-gdpr/key-definitions/what-is-personal-data/">personal information</a></p>
                        </div>

                        <button type="submit" class="ds_button  ds_no-margin">Send feedback</button>
                    </div>
                </div>
            </div>
        </fieldset>
    </form>
</section>
</#if>
