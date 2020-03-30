from algosdk import algod

def pick_client(network):
    """
    Return a client connected to the supplied network.
    """

    algod_token = ""
    headers = {
    "X-API-Key": "" # Add your API token here
    }

    if network == "mainnet":
        algod_address = "https://mainnet-algorand.api.purestake.io/ps1"
    elif network == "testnet":
        algod_address = "https://testnet-algorand.api.purestake.io/ps1"
    elif network == "betanet":
        algod_address = "https://betanet-algorand.api.purestake.io/ps1"
    algod_client = algod.AlgodClient(algod_token, algod_address, headers)
    return algod_client

def find_assets(asset_id, networks=["mainnet", "testnet", "betanet"]):
    """
    Returns a list of clients for each network on which the specified asset ID 
    exists.
    """

    asset_info_list = []
    for network in networks:
        client = pick_client(network)
        try:
            asset_info = client.asset_info(asset_id)
            asset_info_list.append((network, asset_info))
        except:
            pass
    return asset_info_list

def balance_formatter(asset_info):
    """
    Returns the formatted units for a given asset and amount. 
    """

    decimals = asset_info.get("decimals")
    unit = asset_info.get("unitname")
    total = asset_info.get("total")
    formatted_amount = total/10**decimals
    return "{} {}".format(formatted_amount, unit)

