# -*- coding: utf-8 -*-

import logging
import prompts
import config

from ask_sdk_core.skill_builder import SkillBuilder
from ask_sdk_core.utils import is_request_type, is_intent_name
from ask_sdk_core.handler_input import HandlerInput

from ask_sdk_model.ui import SimpleCard
from ask_sdk_model import Response

from algorand import *
from util import *
from prompts import card_format


sb = SkillBuilder()

logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)

@sb.request_handler(can_handle_func=is_request_type("LaunchRequest"))
def launch_request_handler(handler_input):
    """Handler for Skill Launch."""
    # type: (HandlerInput) -> Response
    speech_text = prompts.WELCOME.format(prompts.randomize_examples())
    return handler_input.response_builder.speak(speech_text).set_card(
        SimpleCard("Algorand", card_format(speech_text))).set_should_end_session(
        False).response

@sb.request_handler(can_handle_func=is_intent_name("BlockTimeIntent"))
def block_time_intent_handler(handler_input):
    """Handler for Block Time Intent."""
    # type: (HandlerInput) -> Response
    try:
        network = handler_input.request_envelope.request.intent.slots["NETWORK"].resolutions.resolutions_per_authority[0].values[0].value.id
    except (AttributeError, TypeError):
        network = config.DEFAULT_NETWORK
    client = pick_client(network)
    last_round = client.status().get("lastRound")
    speech_text = prompts.LATEST_BLOCK.format(last_round, network)
    return handler_input.response_builder.speak(speech_text).set_card(
        SimpleCard("Current Block", card_format(speech_text))).set_should_end_session(
        True).response

@sb.request_handler(can_handle_func=is_intent_name("PredictBlockIntent"))
@sb.request_handler(can_handle_func=is_intent_name("PredictTimeIntent"))
def predict_block_intent_handler(handler_input):
    """Handler for Predict Block and Time Intents."""
    # type: (HandlerInput) -> Response
    try:
        network = handler_input.request_envelope.request.intent.slots["NETWORK"].resolutions.resolutions_per_authority[0].values[0].value.id    
    except (AttributeError, TypeError):
        network = config.DEFAULT_NETWORK
    client = pick_client(network)
    spb = handler_input.request_envelope.request.intent.slots["SPB"].value
    if not spb:
        spb = config.DEFAULT_SPB
    last_round = client.status().get("lastRound")
    usertz = user_timezone(handler_input)
    intent_name = handler_input.request_envelope.request.intent.name
    if intent_name == "PredictBlockIntent":
        print(handler_input.request_envelope.request.intent.name)
        date = handler_input.request_envelope.request.intent.slots["DATE"].value
        time = handler_input.request_envelope.request.intent.slots["TIME"].value
        predicted_round, dt_formatted = predict_block_from_time(usertz, date, time, last_round, float(spb))
        speech_text = prompts.PREDICT_ROUND.format(network, predicted_round, dt_formatted, spb)
    elif intent_name == "PredictTimeIntent":
        target_round = handler_input.request_envelope.request.intent.slots["ROUND"].value
        dt_formatted = predict_time_from_block(usertz, int(target_round), last_round, float(spb))
        speech_text = prompts.PREDICT_TIME.format(dt_formatted, network, target_round, spb)

    return handler_input.response_builder.speak(speech_text).set_card(
        SimpleCard("Predictions", card_format(speech_text))).set_should_end_session(
        True).response

@sb.request_handler(can_handle_func=is_intent_name("AssetInfoIntent"))
def asset_info_intent_handler(handler_input):
    """Handler for Predict Block and Time Intents."""
    # type: (HandlerInput) -> Response
    try:
        network = handler_input.request_envelope.request.intent.slots["NETWORK"].resolutions.resolutions_per_authority[0].values[0].value.id
    except (AttributeError, TypeError):
        network = None
    asset_id = handler_input.request_envelope.request.intent.slots["ASSETID"].value
    if network:
        # Just check the specific network
        asset_info = find_assets(asset_id, [network])
        if len(asset_info) == 1:
            speech_text = prompts.ASSET_INFO_NETWORK_SPECIFIED.format(asset_info[0],\
                asset_id, asset_info[1].get("assetname"), balance_formatter(asset_info[1]),\
                    asset_info[1].get('defaultfrozen'))
        else:
            speech_text = prompts.NO_ASSET_FOUND.format(asset_id, network)
    else:
        # Check all networks
        asset_info = find_assets(asset_id)
        if len(asset_info) == 0:
            speech_text = prompts.NO_ASSET_FOUND.format(asset_id, "any network")
        else:
            speech_text = prompts.ASSET_INFO_NETWORK_UNSPECIFIED.format(asset_id,\
                listing_items([key for key, value in asset_info], "and")) 
            for nw, info in asset_info:
                speech_text += " " + prompts.ASSET_INFO_NETWORK_SPECIFIED.format(nw,\
                asset_id, info.get("assetname"), balance_formatter(info),\
                    info.get('defaultfrozen'))
    return handler_input.response_builder.speak(speech_text).set_card(
        SimpleCard("Asset Info", card_format(speech_text))).set_should_end_session(
        True).response

@sb.request_handler(can_handle_func=is_intent_name("AMAZON.HelpIntent"))
def help_intent_handler(handler_input):
    """Handler for Help Intent."""
    # type: (HandlerInput) -> Response
    speech_text = prompts.GENERIC_HELP
    return handler_input.response_builder.speak(speech_text).ask(
        speech_text).set_card(SimpleCard(
            "Algorand", card_format(speech_text))).response

@sb.request_handler(
    can_handle_func=lambda handler_input:
        is_intent_name("AMAZON.CancelIntent")(handler_input) or
        is_intent_name("AMAZON.StopIntent")(handler_input))
def cancel_and_stop_intent_handler(handler_input):
    """Single handler for Cancel and Stop Intent."""
    # type: (HandlerInput) -> Response
    speech_text = "Goodbye!"

    return handler_input.response_builder.speak(speech_text).set_card(
        SimpleCard("Algorand", card_format(speech_text))).response


@sb.request_handler(can_handle_func=is_intent_name("AMAZON.FallbackIntent"))
def fallback_handler(handler_input):
    """
    This handler will not be triggered except in supported locales,
    so it is safe to deploy on any locale.
    """
    # type: (HandlerInput) -> Response
    speech_text = prompts.FALLBACK.format(prompts.randomize_examples())
    reprompt = prompts.FALLBACK_REPROMPT.format(prompts.randomize_examples())
    handler_input.response_builder.speak(speech_text).ask(reprompt)
    return handler_input.response_builder.response


@sb.request_handler(can_handle_func=is_request_type("SessionEndedRequest"))
def session_ended_request_handler(handler_input):
    """Handler for Session End."""
    # type: (HandlerInput) -> Response
    return handler_input.response_builder.response


@sb.exception_handler(can_handle_func=lambda i, e: True)
def all_exception_handler(handler_input, exception):
    """Catch all exception handler"""

    # type: (HandlerInput, Exception) -> Response
    logger.error(exception, exc_info=True)

    speech_text = prompts.ERROR
    handler_input.response_builder.speak(speech_text).ask(speech_text)

    return handler_input.response_builder.response


handler = sb.lambda_handler()


