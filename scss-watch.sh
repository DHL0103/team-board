#!/bin/bash

SCSS_DIR="src/main/resources/static/scss"
CSS_DIR="src/main/resources/static/css"

if [ "$1" = "--once" ]; then
    echo "SCSS 컴파일 중..."
    sass "$SCSS_DIR/style.scss" "$CSS_DIR/style.css"
    echo "완료."
else
    echo "SCSS watch 모드 시작 (종료: Ctrl+C)"
    sass --watch "$SCSS_DIR:$CSS_DIR"
fi