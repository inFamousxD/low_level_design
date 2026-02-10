//
// Created by Aaditya on 2/9/26.
//

#ifndef LOW_LEVEL_DESIGN_TEXTELEMENT_H
#define LOW_LEVEL_DESIGN_TEXTELEMENT_H
#include "DocumentElement.h"


class TextElement: public DocumentElement {
public:
    std::string render() override;

    explicit TextElement(const std::string &text);
private:
    std::string text;
};


#endif //LOW_LEVEL_DESIGN_TEXTELEMENT_H