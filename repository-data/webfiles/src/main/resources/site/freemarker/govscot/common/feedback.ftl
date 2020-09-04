<#if feedbackIsEnabled?? && feedbackIsEnabled = true>

<section id="feedback" class="feedback">
    <form id="feedback-form" class="feedback__form">
        <input id="page-category" type="hidden" value="${layoutName}" />

        <h2 class="gamma">Was this helpful?</h2>
        <p>Your feedback will help us improve this site</p>

        <div class="input-group">
          <fieldset>
            <legend class="hidden">Feedback type</legend>
            <input title="This page was helpful" type="radio" class="js-feedback-type" id="feedback-type-yes" data-gtm="fdbk-yes" name="feedback-type" value="yes"/><label class="feedback__radio-label" for="feedback-type-yes">Yes</label>

            <input title="This page was not helpful" type="radio" class="js-feedback-type" id="feedback-type-no" data-gtm="fdbk-no" name="feedback-type" value="no"/><label class="feedback__radio-label" for="feedback-type-no">No</label>

            <input title="This page was helpful but" type="radio" class="js-feedback-type" id="feedback-type-yesbut" data-gtm="fdbk-yesbut" name="feedback-type" value="yesbut"/><label class="feedback__radio-label" for="feedback-type-yesbut">Yes, but</label>
          </fieldset>
        </div>

        <div class="grid"><!--
            --><div class="grid__item medium--nine-twelfths">
            <div class="input-group">
                <label class="fully-hidden feedback__label feedback__field feedback__field--no feedback__field--yesbut">Choose a reason for your feedback</label>
                <select data-message="Please select a reason" title="Choose reason for your feedback" class="fully-hidden feedback__field feedback__select feedback__field--no required--no reason">
                    <option value="" selected disabled>Please select a reason</option>
                    <option>It wasn't detailed enough</option>
                    <option>It's hard to understand</option>
                    <option>It's incorrect</option>
                    <option>It needs updating</option>
                    <option>There's a broken link</option>
                    <option>It wasn't what I was looking for</option>
                    <option>Other</option>
                </select>

                <select data-message="Please select a reason" title="Choose reason for your feedback" class="fully-hidden feedback__field feedback__select feedback__field--yesbut required--yesbut reason">
                    <option value="" selected disabled>Please select a reason</option>
                    <option>It needs updating</option>
                    <option>There's a spelling mistake</option>
                    <option>It's hard to understand</option>
                    <option>There's a broken link</option>
                    <option>Other</option>
                </select>
            </div>

            <div class="input-group">
                <label class="fully-hidden feedback__label feedback__field feedback__field--yes feedback__field--no feedback__field--yesbut" for="feedback-comment">Your comments</label>
                <textarea data-message="Please enter a comment" maxlength="250" title="Your comments - please do not enter any personal information" rows="5" id="feedback-comment" placeholder="Your comments - please do not enter any personal information" class="fully-hidden comments required--no required--yesbut feedback__field feedback__field--yes feedback__field--no feedback__field--yesbut"></textarea>
            </div>
            </div><!--
        --></div>


        <div class="grid"><!--
            --><div class="grid__item medium--six-twelfths">
                <div class="feedback__field feedback__field--yes feedback__field--no feedback__field--yesbut fully-hidden submit">
                    <button type="submit" class="button button--primary" data-gtm="fdbk-send">Send</button>
                </div>
            </div><!--
        --></div>
    </form>

    <p class="feedback__thanks hidden"><strong>Thanks for your feedback</strong></p>
</section>
</#if>
