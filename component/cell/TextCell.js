import React, {Component} from 'react';

import {
    FlatList,
    StyleSheet,
    Text,
    TouchableHighlight,
    View,
    Image,
    Platform, // 判断当前运行的系统
    Navigator,
} from 'react-native';
import {conversionTime} from '../util/Utils';

export default class TextCell extends Component {
    render() {
        let {movie} = this.props;
        return (
            <TouchableHighlight  onPress={this.props.onPress}>
                <View style={styles.container}>
                    {/*标题*/}
                    <Text numberOfLines={2} style={styles.title}>{movie.title}</Text>
                    {/*下边那行*/}
                    <View style={styles.bottomContainer}>
                        {/*作者*/}
                        <Text style={styles.textBottom}>{movie.author}  </Text>
                        {/*评论数量*/}
                        <Text style={styles.textBottom}>{movie.commentCount}评论 </Text>
                        {/*评论时间*/}
                        <Text style={styles.textBottom}>{conversionTime(movie.behotTime * 1000)}</Text>
                    </View>
                </View>
            </TouchableHighlight>
        );
    }
}
const styles = StyleSheet.create({
    container: {
        // 决定其子元素沿着主轴的排列方式
        justifyContent: 'center',
        // 决定其子元素沿着次轴的排列方式
        // alignItems: 'center',
        backgroundColor: '#ffffff',
        padding: 10,
        borderBottomWidth: 1,
        borderColor: '#e0e0e0',
        minHeight: 90,
    },
    title: {
        backgroundColor: '#ffffff',
        fontSize: 14,
        fontWeight: 'bold',
        color: '#333333',
        textAlign: 'left',
    },
    bottomContainer: {
        // 横向布局
        flexDirection: 'row',
        // 决定其子元素沿着主轴的排列方式
        justifyContent: 'flex-start',
    },
    textBottom: {
        fontSize: 10,
        textAlign: 'left',
        color: '#999999',
        marginTop: 10,
    },
});
