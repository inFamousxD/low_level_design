//
// Created by Aaditya on 2/3/26.
//

#include <iostream>
#include <ostream>
#include <string>
using namespace std;

class ProcessTransaction {
public:
    virtual void process();

    virtual ~ProcessTransaction() = default;
};

class ValidatePayment {
public:
    virtual bool validate() = 0;

    virtual ~ValidatePayment() = default;
};

class ValidateCardPayment : public ValidatePayment {
    string cardId;
public:
    explicit ValidateCardPayment(const string& cardId) {
        this->cardId = cardId;
    }

    bool validate() override {
        if (cardId.size() == 16) return true;

        return false;
    }
};

class ValidateBankPayment : public ValidatePayment {
    string bankId;
public:
    bool validate() override {
        if (bankId.size() == 12) return true;

        return false;
    }
};

int main() {
    ValidatePayment* payment = new ValidateCardPayment("1234123412341234");
    const bool validation = payment->validate();
    cout << validation << endl;
    return 0;
}