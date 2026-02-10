//
// Created by Aaditya on 2/9/26.
//

#include "TextElement.h"

std::string TextElement::render() {
    return this->text;
}

TextElement::TextElement(const std::string &text) {
    this->text = text;
}