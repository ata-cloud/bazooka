import React, { Fragment } from 'react';
import { Card, Dropdown, Menu, Icon, Tag } from "antd";
import styles from "../../index.less";
import stylesService from './index.less';
import { COLOR_SHOW, APP_STATUS } from '@/common/constant';

class ServiceItem extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      envDate: [{}, {}, {}, {}],
      currentId: ''
    }
  }
  static defaultProps = {
    toTop: () => { },
    onItemClick: () => { }
  };
  //置顶
  onTop = () => {
    const { toTop } = this.props;
    toTop();
  }
  onArrowClick = (type) => {
    const { item } = this.props;
    let ele = document.getElementById(item.id);
    let scrollW = ele.scrollWidth, scrollL = ele.scrollLeft;
    let list = item.appEnvInfoVoList && item.appEnvInfoVoList.length > 0 ? item.appEnvInfoVoList : [];
    if (!list.length) {
      return
    }
    switch (type) {
      case 'left': {
        if (scrollL === 0) {
          return
        }
        ele.scrollLeft = scrollL - scrollW / list.length;
      } break;
      case 'right': {
        if (scrollL === scrollW / list.length * 3) {
          return
        }
        ele.scrollLeft = scrollL + scrollW / list.length;
      } break;
    }

  }
  renderMoreOpera(item) {
    return (
      <Menu>
        <Menu.Item onClick={() => { this.onTop() }}>
          <span>{item.orderId > 0 ? '取消置顶' : '置顶'}</span>
        </Menu.Item>
      </Menu>
    )
  }
  render() {
    const { item, onItemClick } = this.props;
    const { envDate } = this.state;
    return (
      <Card className={`${styles.marginB} ${styles.listItem}`} hoverable>
        <div>
          <div className={`${styles.flex} ${stylesService.itemWarp}`}>
            <p className={stylesService.listItemProject} style={{ backgroundColor: COLOR_SHOW[(item.projectId) % 5] }}>{item.projectName}</p>
            <div onClick={() => { this.onTop() }} title={item.orderId > 0 ? '取消置顶' : '置顶'}>
              <Icon type="pushpin" theme={item.orderId > 0 ? "filled" : "outlined"} style={{ fontSize: 18 }} />
            </div>
          </div>
          <div onClick={() => onItemClick()}>
            <div className={stylesService.listItemAppName}>{item.appName}</div>
            <div className={styles.itemDesc}>
              <span className={`${styles.itemDescSpan} ${styles.textC}`}>{item.description}</span>
            </div>
          </div>
          <div className={stylesService.listItemEnvBox}>
            <div className={stylesService.listItemEnv} id={item.id}>
              {
                item.appEnvInfoVoList && item.appEnvInfoVoList.map((vo, i) => (
                  <div key={i} className={stylesService.envItem}>
                    <div className={stylesService.envItemName}>{vo.envName}</div>
                    <p className={`${styles[APP_STATUS[vo.status].color]} ${stylesService.textStatus}`}>
                      {APP_STATUS[vo.status].text}
                    </p>

                  </div>
                ))
              }
            </div>
            {
              item.appEnvInfoVoList &&
              <Fragment>
                <div className={stylesService.leftArrow} onClick={() => { this.onArrowClick('left') }}>
                  <Icon type="left" style={{ color: "#bbb" }} />
                </div>
                <div className={stylesService.rightArrow}>
                  <Icon type="right" style={{ color: "#bbb" }} onClick={() => { this.onArrowClick('right') }} />
                </div>
              </Fragment>
            }
          </div>
        </div>
        {/* <div onClick={() => onItemClick()}>
          <div className={styles.flexCenter}>
            <div className={styles.itemFirst} style={{ backgroundColor: COLOR_SHOW[(item.id) % 5] }}>{item.appName && item.appName.charAt(0).toUpperCase()}</div>
            <div className={styles.appName}>{item.projectName} / {item.appName}</div>
            {
              item.orderId > 0 &&
              <div className={styles.marginL10}>
                <Tag color="blue">置顶</Tag>
              </div>
            }

          </div>
          <div className={styles.itemDesc}>
            <span className={styles.itemDescSpan}>{item.description}</span>
          </div>
        </div>
        <div className={styles.moreOprea}>
          <Dropdown overlay={this.renderMoreOpera(item)} placement="bottomCenter">
            <Icon type="ellipsis" style={{ fontSize: 25 }} />
          </Dropdown>
        </div> */}
      </Card>
    )
  }

}
export default ServiceItem;