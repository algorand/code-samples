import requests
from pytz import timezone, utc
from datetime import datetime, timedelta
from ask_sdk_core.handler_input import HandlerInput

# Functions to format and predict clock and round times

def user_timezone(handler_input):
    """
    Returns the current local time for the user based on their 
    timezone setting.
    """

    # get device id
    sys_object = handler_input.request_envelope.context.system
    device_id = sys_object.device.device_id

    # get systems api information
    api_endpoint = sys_object.api_endpoint
    api_access_token = sys_object.api_access_token

    # construct systems api timezone url
    url = '{api_endpoint}/v2/devices/{device_id}/settings/System.timeZone'.format(api_endpoint=api_endpoint, device_id=device_id)
    headers = {'Authorization': 'Bearer ' + api_access_token}

    usertz = ""
    try:
        r = requests.get(url, headers=headers)
        res = r.json()
        print(res)
        #logger.info("Device API result: {}".format(str(res)))
        usertz = res
        print(usertz)
        return usertz
    except Exception:
        print("Exception returning users timezone.")
        handler_input.response_builder.speak("There was a problem connecting to the service")
        return handler_input.response_builder.response  

def predict_block_from_time(usertz, target_date, target_time, current_block, spb):
    """
    Predict the round expected to occur at the date and time specified.
    """

    alexa_dt = alexa_datetime(target_date, target_time)
    formatted_dt = format_dt_prompt(alexa_dt)
    target_datetime_utc = localtime_to_utc(usertz, alexa_dt)
    utcnow = datetime.utcnow().astimezone(utc)
    delta = target_datetime_utc - utcnow
    seconds = delta.total_seconds()
    blocks = round(seconds/spb)
    predicted_block = current_block + blocks
    return (predicted_block, formatted_dt)

def predict_time_from_block(usertz, target_block, current_block, spb):
    """
    Predict the clock time expected at the target round.
    """
    
    utcnow = datetime.utcnow().astimezone(utc)
    delta = target_block - current_block
    total_seconds = delta*spb
    predicted_time_utc = utcnow + timedelta(seconds=total_seconds)
    predicted_time_local = utc_to_localtime(usertz, predicted_time_utc)
    formatted_dt = format_dt_prompt(predicted_time_local)
    return formatted_dt

def localtime_to_utc(usertz, local_dt):
    """
    Convert the specified datetime to UTC. Used in
    conjunction with PredictBlockIntent.
    """

    tz = timezone(usertz)
    print(tz)
    local_dt = tz.localize(local_dt)
    utc_dt = local_dt.astimezone(utc)
    return utc_dt

def utc_to_localtime(usertz, utc_dt):
    """Convert the UTC datetime to the user's local time."""

    tz = timezone(usertz)
    local_dt = utc_dt.astimezone(tz)
    return local_dt

def alexa_datetime(date, time):
    """Return Alexa date and time strings as a datetime object."""
    return datetime.strptime(date + " " + time, "%Y-%m-%d %H:%M")

def format_dt_prompt(dt):
    """Formats datetime for Alexa to say within a prompt."""

    day_of_week = datetime.strftime(dt, "%A")
    month = datetime.strftime(dt, "%B")
    day = en_us_suffix(int(datetime.strftime(dt, "%d")))
    year = datetime.strftime(dt, "%Y")
    hour = en_us_hour(dt)
    tz_str = datetime.strftime(dt, "%Z")
    if len(tz_str) < 2:
        tz_str = "local"
    print(tz_str)
    formatted = "{}, {} {} {} at {} {} time".format(day_of_week, month, day, year, hour, tz_str)
    print(formatted)
    return formatted

# Formatting Functions for fluent Alexa prompts.
en_us_suffix = lambda n: "%d%s"%(n,{1:"st",2:"nd",3:"rd"}.get(n if n<20 else n%10,"th"))
en_us_hour = lambda dt: str(int(datetime.strftime(dt, "%I"))) + datetime.strftime(dt, ":%M %p") 
listing_items = lambda l, conj: ", ".join(l[:-1]) + " " + conj + " " + l[-1] if len(l) > 2 \
    else " {} ".format(conj).join(l)

    
