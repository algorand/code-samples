import re
import random

def randomize_examples():
    return random.choice(
        [
            "What is the latest round on MainNet?",
            "What is the latest round on TestNet?",
            "What is the latest round on BetaNet?",
            "Tell me about asset <say-as interpret-as='digits'>312769</say-as>.",
            "Asset <say-as interpret-as='digits'>219501</say-as>.",
            "Predict the round tomorrow at 10 PM on TestNet.",
            "Predict the round next Sunday at 10 PM on MainNet.",
            "Tell me when round 10,000,000 occurs."
        ]
    )

def card_format(speech_text):
    return re.sub("\<[^\<\>]+?\>", "", speech_text)

WELCOME = "Welcome to Algorand on Alexa. I can give you information about MainNet, TestNet, or BetaNet. For example, you can say: {}"

WHICH_NETWORK = "On MainNet, TestNet, or BetaNet?"

LATEST_BLOCK = "Round {} just passed on {}."

PREDICT_ROUND = "{} round {} is estimated to occur on {}, assuming blocks produced every {} seconds." # network, block, datetime_formatted, spb
PREDICT_TIME = "{} is when {} block {} is predicted to occur, assuming an average block time of {} seconds." # datetime_formatted, network, block, spb

ASSET_INFO_NETWORK_SPECIFIED = "Info for {} Asset {}. Asset name: {}, Total Supply: {}, Default frozen state is {}."
ASSET_INFO_NETWORK_UNSPECIFIED = "Found asset {} on {}. "
NO_ASSET_FOUND = "I couldn't find asset {} on {}."

GENERIC_HELP = "I can tell you the latest round on any network. I can predict the round at a specific clock time or the clock time at a specific round. I can also give you the details of an asset if you tell me the asset ID. "

FALLBACK = "The Algorand skill can't help with that. Try saying {}"

FALLBACK_REPROMPT = "Try saying {}"

ERROR = "Sorry, there was a problem. Please try again."


