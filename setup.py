from setuptools import setup, find_packages

setup(
    name="trolley-pi",
    version="0.1.0",
    packages=find_packages(),
    install_requires=[
        "requests",
        "RPi.GPIO",
        "hx711",
        "pyserial",
    ],
    python_requires=">=3.7",
) 